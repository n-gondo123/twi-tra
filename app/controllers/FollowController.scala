package controllers

import controllers.Role.NormalUser
import jp.t2v.lab.play2.auth.AuthElement
import models.Tables._
import models.Tables.profile.simple._
import play.api.db.slick._
import play.api.mvc._
import play.api.Play.current

object FollowController extends Controller with AuthElement with AuthConfigImpl {

  /**
   * フォロー
   */
  def watch(id: Int) = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    DB.withSession { implicit session =>
      val filterFollow =
        Follow
          .filter(f => f.userId === loggedIn.id && f.followUserId === id)

      filterFollow.firstOption.map {existFollow =>
        filterFollow
          .map(_.flag)
          .update(true)
      }.getOrElse {
        val follow = FollowRow(0, loggedIn.id, id, true, null, null)
        Follow.insert(follow)
      }
    }
    Redirect(routes.TwiUserController.list())
  }

  /**
   * アンフォロー
   */
  def unfollow(id: Int) = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    DB.withSession { implicit session =>
      Follow
        .filter(f => f.userId === loggedIn.id && f.followUserId === id)
        .map(_.flag)
        .update(false)
    }
    Redirect(routes.TwiUserController.list())
  }
}
