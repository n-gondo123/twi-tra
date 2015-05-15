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
      "name" -> nonEmptyText(maxLength = 20),
      "email" -> nonEmptyText(maxLength = 100),
      "password" -> nonEmptyText(maxLength = 20)
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
//    val (form, title) = rs.session.get("userId").map { userId =>
//      val user = TwiUser.filter(_.id === userId.toInt).first
//      userForm.fill(UserForm(user.name, user.email, user.password))
//      (userForm, "アカウント編集")
//    }.getOrElse {
//      (userForm, "アカウント登録")
//    }

    Ok(views.html.user.edit(form, title))
  }

  /**
   * 登録実行
   */
  def create = DBAction.transaction { implicit rs =>
    userForm.bindFromRequest.fold(
      error => BadRequest(views.html.user.edit(error, "hoge")),
      form => {
        val user = TwiUserRow(3, form.name, form.email, form.password, null, null)
        TwiUser.insert(user)

        Redirect(routes.SignController.index)
      }
    )
  }

  /**
   * 更新実行
   */
  def update = DBAction.transaction { implicit rs =>
    userForm.bindFromRequest.fold(
      error => BadRequest(views.html.user.edit(error, "hoge")),
      form => {
        val userId = rs.session.get("userId").get.toInt
        val user = TwiUserRow(userId, form.name, form.email, form.password, null, null)
        TwiUser.filter(_.id === userId).update(user)

        Redirect(routes.TwiUserController.list)
      }
    )
  }
//
//  /**
//   * 削除実行
//   */
//  def remove(id: Long) = DBAction.transaction { implicit rs =>
//    Users.filter(t => t.id === id.bind).delete
//
//    Redirect(routes.UserController.list)
//  }
}
