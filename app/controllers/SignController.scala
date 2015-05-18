package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.db.slick._
import play.api.libs.Crypto._
import jp.t2v.lab.play2.auth.{OptionalAuthElement, LoginLogout}

import models.Tables._
import profile.simple._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play.current

object SignController extends Controller with LoginLogout with OptionalAuthElement with AuthConfigImpl {

  // フォームの値を格納する
  case class SignForm(name: String, password: String)

  // formのデータとケースクラスの変換を行う
  val signForm = Form(
    mapping(
      "name" -> nonEmptyText(minLength = 3, maxLength = 20),
      "password" -> nonEmptyText(minLength = 3, maxLength = 20)
    )(SignForm.apply)(SignForm.unapply)
  )

  /**
   * サインインインデックス
   */
  def index = StackAction { implicit rs =>
    loggedIn.map { logIn =>
      Redirect(routes.Application.index())
    }.getOrElse {
      Ok(views.html.sign(signForm, ""))
    }
  }

  def validate(signForm: SignForm) = {
    DB.withSession { implicit session =>
      TwiUser
        .filter(_.name === signForm.name)
        .firstOption
//        .exists(_.password == encryptAES(signForm.password))
        .map(_.password == encryptAES(signForm.password))
        .getOrElse(false)
    }
  }

  def signIn = Action { implicit rs =>
    Ok(views.html.sign(signForm, "hoge"))
  }

//  /**
//   * サインイン
//   */
//  def signIn = Action { implicit rs =>
//    signForm.bindFromRequest.fold(
//      error => BadRequest(views.html.sign(error, "入力内容に誤りがあります")),
//      form => {
//        val password = encryptAES(form.password)
//        val user =
//          TwiUser
//            .filter(_.name === form.name)
//            .filter(_.password === password).firstOption
//
//        if (user.isDefined) {
//          Redirect(routes.Application.index).withSession {
//            "userId" -> user.get.id.toString
//          }
//        } else {
//          val reqForm = signForm.fill(SignForm(form.name, form.password))
//          Ok(views.html.sign(reqForm, "ユーザー名またはパスワードが違います"))
//        }
//      }
//    )
//  }

  /**
   * サインアウト
   */
  def signOut = Action.async { implicit rs =>
    gotoLogoutSucceeded
  }

  /**
   * 認証
   */
  def authenticate = Action.async { implicit rs =>
    signForm.bindFromRequest.fold(
      error => {
        DB.withSession { implicit session =>
          Future.successful(BadRequest(views.html.sign(error, "入力内容に誤りがあります")))
        }
      },
      form => gotoLoginSucceeded(form.name)
    )
  }
}
