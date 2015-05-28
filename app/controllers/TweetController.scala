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

import play.api.libs.json._


object TweetController extends Controller with AuthElement with AuthConfigImpl {

  // フォームの値を格納する
  case class TweetForm(content: String)

  // formのデータとケースクラスの変換を行う
  val tweetForm = Form(
    mapping(
      "content" -> nonEmptyText(maxLength = 140)
    )(TweetForm.apply)(TweetForm.unapply)
  )

  implicit val userFromReads = Json.reads[TweetForm]
  implicit val userRowWriter = Json.writes[TweetRow]

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
    Ok(views.html.tweet.list())
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
        .filter(t => t.userId === user.id && t.id === id.bind)
        .delete

      Redirect(routes.TweetController.list())
    }
  }
}
