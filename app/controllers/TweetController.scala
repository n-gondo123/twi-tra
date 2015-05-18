package controllers

import models.Tables._
import models.Tables.profile.simple._
import play.api.data.Forms._
import play.api.data._
import play.api.db.slick._
import play.api.mvc._

object TweetController extends Controller {

  // フォームの値を格納する
  case class TweetForm(content: String)

  // formのデータとケースクラスの変換を行う
  val tweetForm = Form(
    mapping(
      "content" -> nonEmptyText(maxLength = 140)
    )(TweetForm.apply)(TweetForm.unapply)
  )

  /**
   * ツイートフォーム
   */
  def edit = Action { implicit rs =>
    Ok(views.html.tweet.edit(tweetForm, "Please Input"))
  }

  /**
   * 一覧表示(自分のみ)
   */
  def list = DBAction { implicit rs =>
    rs.session.get("userId").map { userId =>
      val tweets =
        Tweet
          .filter(_.userId === userId.toInt)
          .innerJoin(TwiUser).on { (t, u) =>
            t.userId === u.id
          }
          .map { case (t, u) =>
            (u.name, t)
          }
          .sortBy(_._2.insTime.desc)
          .list

      Ok(views.html.tweet.list(tweets))
    }.getOrElse {
      Redirect(routes.Application.index)
    }
  }

  /**
   * 一覧表示(フォロワー含む)
   */
  def all(user: TwiUserRow) = DBAction { implicit rs =>
    val userIdInt = user.id
    val tweets =
      Tweet
        .innerJoin(TwiUser).on { (t, u) =>
          t.userId === u.id
        }
        .filter { case (t, u) =>
          (t.userId in
            Follow
              .filter(_.userId === userIdInt)
              .map(_.followUserId)
            ) || (t.userId === userIdInt)
        }
        .map { case(t, u) =>
          (u.name, t)
        }
        .sortBy(_._2.insTime desc)
        .list

    Ok(views.html.tweet.list(tweets))
  }

  /**
   * 登録実行
   */
  def create = DBAction.transaction { implicit rs =>
    tweetForm.bindFromRequest.fold(
      error => BadRequest(views.html.tweet.edit(error, "hoge")),
      form => {
        val tweet = TweetRow(0, rs.session.get("userId").get.toInt, form.content, null, null)
        Tweet.insert(tweet)

        Redirect(routes.TweetController.edit)
      }
    )
  }

  /**
   * 削除実行
   */
  def remove(id: Int) = DBAction.transaction { implicit rs =>
    rs.session.get("userId").map { userId =>
      Tweet
        .filter(_.userId === userId.toInt)
        .filter(t => t.id === id.bind)
        .delete

      Redirect(routes.TweetController.list)
    }.getOrElse{
      Redirect(routes.TweetController.list)
    }
  }
}
