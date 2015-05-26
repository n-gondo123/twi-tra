package controllers

import jp.t2v.lab.play2.auth._
import play.api.mvc._

object Application extends Controller with LoginLogout with OptionalAuthElement with AuthConfigImpl {

  def index = StackAction { implicit request =>
    Redirect(routes.Application.home())
  }

  /**
   * ホーム画面表示
   */
  def home = StackAction { implicit request =>
    loggedIn.map {user =>
      val list = TweetController.all(user)
      Ok(views.html.tweet.list(list))
    }.getOrElse {
      Redirect(routes.SignController.index())
    }
  }
}

