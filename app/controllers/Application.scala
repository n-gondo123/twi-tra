package controllers

import jp.t2v.lab.play2.auth._
import play.api.mvc._

object Application extends Controller with OptionalAuthElement with AuthConfigImpl {

//  implicit def toAttributePair[B](pair: (String, B)): (Symbol, B) = Symbol(pair._1) -> pair._2
//
  def index = StackAction { implicit request =>
    Redirect(routes.Application.home())
  }

  /**
   * ホーム画面表示
   */
  def home = StackAction { implicit request =>
    loggedIn.map {user =>
      Ok(views.html.tweet.list(user.name))
    }.getOrElse {
      Redirect(routes.SignController.index())
    }
  }
}
