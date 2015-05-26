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


object TweetController extends Controller with AuthElement with AuthConfigImpl {

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
  def edit = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    Ok(views.html.tweet.edit(tweetForm, "ツイートフォーム"))
  }

  /**
   * 一覧表示(自分のみ)
   */
  def list = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    DB.withSession { implicit session =>
      val user = loggedIn
      val tweets =
        Tweet
          .filter(_.userId === user.id)
          .innerJoin(TwiUser).on { (t, u) =>
            t.userId === u.id
          }
          .map { case (t, u) =>
            (u.name, t)
          }
          .sortBy(_._2.insTime.desc)
          .list

      Ok(views.html.tweet.list(tweets))
    }
  }

  /**
   * 一覧表示(フォロワー含む)
   */
  def all(user: TwiUserRow) = {
    DB.withSession { implicit session =>
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
          .map { case (t, u) =>
            (u.name, t)
          }
          .sortBy(_._2.insTime.desc)
          .list
      tweets
    }
  }

  /**
   * 登録実行
   */
  def create = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    tweetForm.bindFromRequest.fold(
      error => BadRequest(views.html.tweet.edit(error, "hoge")),
      form => {
        val user = loggedIn
        DB.withSession { implicit session =>
          val tweet = TweetRow(0, user.id, form.content, null, null)
          Tweet.insert(tweet)
        }
        Redirect(routes.TweetController.edit())
      }
    )
  }

  /**
   * 削除実行
   */
  def remove(id: Int) = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    val user = loggedIn
    DB.withSession { implicit session =>
      Tweet
        .filter(_.userId === user.id)
        .filter(t => t.id === id.bind)
        .delete

      Redirect(routes.TweetController.list())
    }
  }
}
