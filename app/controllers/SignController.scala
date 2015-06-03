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
      Ok(views.html.sign(signForm, "", ""))
    }
  }

  /**
   * 認証（サインイン）
   */
  def signIn = Action.async { implicit rs =>
    signForm.bindFromRequest.fold(
      error => {
        Future.successful(BadRequest(views.html.sign(error, "入力形式が正しくありません", "")))
      },
      form => {
        existsOrEmpty(form).map { user =>
          gotoLoginSucceeded(user.id)
        }.getOrElse {
          Future.successful(Ok(views.html.sign(signForm.fill(form), "ユーザー名、またはパスワードが違います", "")))
        }
      }
    )
  }

  /**
   * ログインフォームvalidation
   */
  def existsOrEmpty(signForm: SignForm) = {
    DB.withSession { implicit session =>
      TwiUser
        .filter(u => u.name === signForm.name && u.password === encryptAES(signForm.password))
        .firstOption
    }
  }

  /**
   * サインアウト
   */
  def signOut = Action.async { implicit rs =>
    gotoLogoutSucceeded
  }
}
