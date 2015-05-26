package controllers

import controllers.Role._
import jp.t2v.lab.play2.auth._
import play.api.mvc._

object Application extends Controller with LoginLogout with AuthElement with AuthConfigImpl {

  def index = StackAction { implicit request =>
    Redirect(routes.Application.home())
  }

  /**
   * ホーム画面表示
   */
  def home = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    val list = TweetController.all(loggedIn)
    Ok(views.html.tweet.list(list))
  }
}
