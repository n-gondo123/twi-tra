package controllers

import controllers.Role._
import jp.t2v.lab.play2.auth.AuthElement
import play.api.mvc._

object Application extends Controller with AuthElement with AuthConfigImpl {

  def index = Action { implicit rs =>
    Redirect(routes.TweetController.all)
  }

  /**
   * ホーム画面表示
   */
  def home = StackAction(AuthorityKey -> NormalUser) { implicit rs =>
    TweetController.all(loggedIn)
  }
}
