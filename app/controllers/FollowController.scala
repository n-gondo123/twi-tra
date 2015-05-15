package controllers

import models.Tables._
import models.Tables.profile.simple._
import play.api.data.Forms._
import play.api.data._
import play.api.db.slick._
import play.api.mvc._

object FollowController extends Controller {

  // フォームの値を格納する
  case class FollowForm(followUserId: String)

  // formのデータとケースクラスの変換を行う
  val followForm = Form(
    mapping(
      "followUserId" -> nonEmptyText(maxLength = 140)
    )(FollowForm.apply)(FollowForm.unapply)
  )

//  /**
//   * 一覧表示
//   */
//  def list = DBAction { implicit rs =>
//    val tweets = Follow.filter(_.userId === rs.session.get("userId").get.toInt).sortBy(_.insTime.desc).list
//
//    Ok(views.html.tweet.list(tweets))
//  }

  /**
   * 登録実行
   */
  def create(id: Int) = DBAction.transaction { implicit rs =>
//    val followUserId = followForm.bindFromRequest.get.followUserId
//    val follow = FollowRow(0, rs.session.get("userId").get.toInt, followUserId.toInt, true, null, null)
    val follow = FollowRow(0, rs.session.get("userId").get.toInt, id, true, null, null)

    Follow.insert(follow)

    Redirect(routes.TwiUserController.list)
  }

  /**
   * 削除実行
   */
  def remove(id: Int) = DBAction.transaction { implicit rs =>
//    val followUserId = followForm.bindFromRequest.get.followUserId.toInt
//    Follow
//      .filter(_.userId === followUserId)
//      .filter(_.followUserId === rs.session.get("userId").get.toInt)
//      .delete
    Follow
      .filter(_.userId === rs.session.get("userId").get.toInt)
      .filter(_.followUserId === id)
      .delete

    Redirect(routes.TwiUserController.list)
  }
}
