package controllers

import play.api.mvc._

object Application extends Controller {

  def index = Action { implicit rs =>
    rs.session.get("userId").map { user =>
      Redirect(routes.TweetController.all)
    }.getOrElse {
      Redirect(routes.SignController.index)
    }
  }

  /**
   * ホーム画面表示
   */
  def home = Action { implicit rs =>
    rs.session.get("userId").map { user =>
      Redirect(routes.TweetController.all)
    }.getOrElse {
      Redirect(routes.SignController.index)
    }
  }
}
