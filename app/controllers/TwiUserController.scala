package controllers

import play.api.mvc._
import play.api.db.slick._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.Crypto._

import models.Tables._
import profile.simple._

object TwiUserController extends Controller {

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
  def list = DBAction { implicit rs =>
    rs.session.get("userId").map { userId =>
      val userIdInt = userId.toInt
      val users =
        TwiUser
          .filterNot(_.id === userIdInt)
          .leftJoin(Follow).on { (u, f1) =>
            (u.id === f1.followUserId) && (f1.userId === userIdInt)
          }
          .leftJoin(Follow).on { case((u, f1), f2) =>
            (u.id === f2.userId) && (f2.followUserId === userIdInt)
          }
          .map { case ((u, f1), f2) =>
            (u, f1.flag.?.getOrElse(false), f2.flag.?.getOrElse(false))
          }
          .sortBy(_._1.id)
      Ok(views.html.user.list(users.list))
    }.getOrElse {
      Redirect(routes.SignController.index)
    }
  }

  /**
   * 登録・編集画面表示
   */
  def edit = DBAction {implicit  rs =>
    val userId = rs.session.get("userId")
    val form = if (userId.isDefined) {
      val user = TwiUser.filter(_.id === userId.get.toInt).first
      userForm.fill(UserForm(user.name, user.email, user.password))
    } else {
      userForm
    }

    val title = if (userId.isDefined) {
      "アカウント編集"
    } else {
      "アカウント登録"
    }

    Ok(views.html.user.edit(form, title, ""))
  }

  /**
   * 登録実行
   */
  def create = DBAction.transaction { implicit rs =>
    userForm.bindFromRequest.fold(
      error => BadRequest(views.html.user.edit(error, "アカウント登録", "入力内容に誤りがあります")),
      form => {
        val reqForm = userForm.fill(UserForm(form.name, form.email, form.password))
        val list = TwiUser.filter(t => (t.name === form.name) || (t.email === form.email)).list
        if (list.nonEmpty) {
          if (list.count(_.name == form.name) > 0) {
            Ok(views.html.user.edit(reqForm, "アカウント登録", "その名前はすでに登録されています"))
          } else {
            Ok(views.html.user.edit(reqForm, "アカウント登録", "そのメールアドレスはすでに登録されています"))
          }
        } else {
          val password = encryptAES(form.password)
          val user = TwiUserRow(0, form.name, form.email, password, null, null)
          TwiUser.insert(user)
          Redirect(routes.SignController.index)
        }
      }
    )
  }

  /**
   * 更新実行
   */
  def update = DBAction.transaction { implicit rs =>
    userForm.bindFromRequest.fold(
      error => BadRequest(views.html.user.edit(error, "アカウント編集", "入力内容に誤りがあります")),
      form => {
        val userIdInt = rs.session.get("userId").get.toInt

        val reqForm = userForm.fill(UserForm(form.name, form.email, form.password))
        val list =
          TwiUser
            .filter(t => (t.name === form.name) || (t.email === form.email))
            .filterNot(_.id === userIdInt)
            .list
        if (list.nonEmpty) {
          if (list.count(_.name == form.name) > 0) {
            Ok(views.html.user.edit(reqForm, "アカウント登録", "その名前はすでに登録されています"))
          } else {
            Ok(views.html.user.edit(reqForm, "アカウント登録", "そのメールアドレスはすでに登録されています"))
          }
        } else {
          val password = encryptAES(form.password)
          val self = TwiUserRow(userIdInt, form.name, form.email, password, null, null)
          TwiUser.filter(_.id === userIdInt).update(self)
          Redirect(routes.SignController.index)
        }
      }
    )
  }
}
