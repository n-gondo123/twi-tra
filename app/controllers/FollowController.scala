package controllers

import controllers.Role.NormalUser
import jp.t2v.lab.play2.auth.AuthElement
import models.Tables._
import models.Tables.profile.simple._
import play.api.data.Forms._
import play.api.data._
import play.api.db.slick._
import play.api.mvc._
import play.api.Play.current

object FollowController extends Controller with AuthElement with AuthConfigImpl {
  /**
   * 登録実行
   */
  def create(id: Int) = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    DB.withSession { implicit session =>
      val follow = FollowRow(0, loggedIn.id, id, true, null, null)

      Follow.insert(follow)
    }
    Redirect(routes.TwiUserController.list())
  }

  /**
   * 削除実行
   */
  def remove(id: Int) = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    DB.withSession { implicit session =>
      Follow
        .filter(_.userId === loggedIn.id)
        .filter(_.followUserId === id)
        .delete
    }
    Redirect(routes.TwiUserController.list())
  }
}
