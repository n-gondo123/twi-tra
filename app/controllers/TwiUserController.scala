package controllers

import jp.t2v.lab.play2.auth.OptionalAuthElement
import play.api.mvc._
import play.api.db.slick._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.Crypto._
import play.api.Play.current

import models.Tables._
import profile.simple._

object TwiUserController extends Controller with OptionalAuthElement with AuthConfigImpl{

  // フォームの値を格納する
  case class UserForm(name: String, email: String, password: String)

  // formのデータとケースクラスの変換を行う
  val userForm = Form(
    mapping(
      "name" -> nonEmptyText(minLength = 3, maxLength = 20),
      "email" -> nonEmptyText(maxLength = 100),
      "password" -> nonEmptyText(minLength = 3, maxLength = 20)
    )(UserForm.apply)(UserForm.unapply)
  )

  /**
   * 一覧表示
   */
  def list = StackAction { implicit request =>
    loggedIn.map { user =>
      DB.withSession { implicit session =>
        val userIdInt = user.id
        val users =
          TwiUser
            .filterNot(_.id === userIdInt)
            .leftJoin(Follow).on { (u, f1) =>
            (u.id === f1.followUserId) && (f1.userId === userIdInt)
          }
            .leftJoin(Follow).on { case ((u, f1), f2) =>
            (u.id === f2.userId) && (f2.followUserId === userIdInt)
          }
            .map { case ((u, f1), f2) =>
            (u, f1.flag.?.getOrElse(false), f2.flag.?.getOrElse(false))
          }
            .sortBy(_._1.id)
        Ok(views.html.user.list(users.list))
      }
    }.getOrElse {
      Unauthorized("許可されていません")
    }
  }

  /**
   * 登録・編集画面表示
   */
  def edit = StackAction { implicit request =>
    val form = if (loggedIn.isDefined) {
      DB.withSession { implicit session =>
        val user = TwiUser.filter(_.id === loggedIn.get.id).first
        userForm.fill(UserForm(user.name, user.email, user.password))
      }
    } else {
      userForm
    }

    val title = if (loggedIn.isDefined) {
      "アカウント編集"
    } else {
      "アカウント登録"
    }

    Ok(views.html.user.edit(form, title, "", loggedIn))
  }

  /**
   * 登録実行
   */
  def create = StackAction { implicit request =>
    userForm.bindFromRequest.fold(
      error => BadRequest(views.html.user.edit(error, "アカウント登録", "入力内容に誤りがあります", loggedIn)),
      form => {
        DB.withSession { implicit session =>
          val reqForm = userForm.fill(UserForm(form.name, form.email, form.password))
          val list = TwiUser.filter(t => (t.name === form.name) || (t.email === form.email)).list
          if (list.nonEmpty) {
            if (list.count(_.name == form.name) > 0) {
              Ok(views.html.user.edit(reqForm, "アカウント登録", "その名前はすでに登録されています", loggedIn))
            } else {
              Ok(views.html.user.edit(reqForm, "アカウント登録", "そのメールアドレスはすでに登録されています", loggedIn))
            }
          } else {
            val password = encryptAES(form.password)
            val user = TwiUserRow(0, form.name, form.email, password, null, null)
            TwiUser.insert(user)
            Redirect(routes.SignController.index())
          }
        }
      }
    )
  }

  /**
   * 更新実行
   */
  def update = StackAction { implicit request =>
    userForm.bindFromRequest.fold(
      error => BadRequest(views.html.user.edit(error, "アカウント編集", "入力内容に誤りがあります", loggedIn)),
      form => {
        loggedIn.map { user =>
          DB.withSession { implicit session =>
            val userIdInt = user.id

            val reqForm = userForm.fill(UserForm(form.name, form.email, form.password))
            val list =
              TwiUser
                .filter(t => (t.name === form.name) || (t.email === form.email))
                .filterNot(_.id === userIdInt)
                .list
            if (list.nonEmpty) {
              if (list.count(_.name == form.name) > 0) {
                Ok(views.html.user.edit(reqForm, "アカウント登録", "その名前はすでに登録されています", loggedIn))
              } else {
                Ok(views.html.user.edit(reqForm, "アカウント登録", "そのメールアドレスはすでに登録されています", loggedIn))
              }
            } else {
              val password = encryptAES(form.password)
              val self = TwiUserRow(userIdInt, form.name, form.email, password, null, null)
              TwiUser.filter(_.id === userIdInt).update(self)
              Redirect(routes.SignController.index())
            }
          }
        }.getOrElse {
          Forbidden("権限がありません")
        }
      }
    )
  }
}
