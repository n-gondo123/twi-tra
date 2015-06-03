package controllers

import controllers.Role.NormalUser
import jp.t2v.lab.play2.auth.AuthElement
import play.api.mvc._

object TweetController extends Controller with AuthElement with AuthConfigImpl {

  /**
   * 一覧表示(自分のみ)
   */
  def list(name: String) = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    Ok(views.html.tweet.list(loggedIn.name))
  }
}
