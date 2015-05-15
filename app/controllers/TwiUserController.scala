package controllers

import play.api.mvc._
import play.api.db.slick._
import play.api.data._
import play.api.data.Forms._

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
      "password" -> nonEmptyText(minLength = 6, maxLength = 20)
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
        val editForm = userForm.fill(UserForm(form.name, form.email, form.password))
        TwiUser.filter(_.name === form.name).firstOption.map { user =>
          Ok(views.html.user.edit(editForm, "アカウント登録", "その名前はすでに登録されています"))
        }.getOrElse {
          TwiUser.filter(_.email === form.email).firstOption.map { user =>
            Ok(views.html.user.edit(editForm, "アカウント登録", "そのメールアドレスはすでに登録されています"))
          }.getOrElse {
            val user = TwiUserRow(0, form.name, form.email, form.password, null, null)
            TwiUser.insert(user)
            Redirect(routes.SignController.index)
          }
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
        val userId = rs.session.get("userId").get.toInt
        val user = TwiUserRow(userId, form.name, form.email, form.password, null, null)
        TwiUser.filter(_.id === userId).update(user)

        Redirect(routes.TwiUserController.list)
      }
    )
  }

//  /**
//   * 相関チェック
//   */
//  def validate(userForm: UserForm): Boolean = {
//
//  }
}
