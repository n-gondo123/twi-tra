package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.db.slick._

import models.Tables._
import profile.simple._

object SignController extends Controller {

  // フォームの値を格納する
  case class SignForm(name: String, password: String)

  // formのデータとケースクラスの変換を行う
  val signForm = Form(
    mapping(
      "name" -> nonEmptyText(maxLength = 20),
      "password" -> nonEmptyText(maxLength = 20)
    )(SignForm.apply)(SignForm.unapply)
  )

  /**
   * サインインインデックス
   */
  def index = Action { implicit rs =>
    rs.session.get("userId").map { userId =>
      Redirect(routes.Application.index)
    }.getOrElse {
      Ok(views.html.sign(signForm, "Please Input"))
    }
  }

  /**
   * サインイン
   */
  def signIn = DBAction { implicit rs =>
    signForm.bindFromRequest.fold(
      error => BadRequest(views.html.sign(error, "error")),
      form => {
        val user =
          TwiUser
            .filter(_.name === form.name)
            .filter(_.password === form.password).firstOption

        if (user.isDefined) {
          Redirect(routes.Application.index).withSession {
            "userId" -> user.get.id.toString
          }
        } else {
          Redirect(routes.SignController.index)
        }
      }
    )
  }

  /**
   * サインアウト
   */
  def signOut = Action {
    Redirect(routes.SignController.index).withNewSession
  }
}
