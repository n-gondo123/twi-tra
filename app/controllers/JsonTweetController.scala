package controllers

import java.sql.Timestamp;

import controllers.Role.NormalUser
import jp.t2v.lab.play2.auth.AuthElement
import models.Tables._
import models.Tables.profile.simple._
import org.joda.time.DateTime
import play.api.data.Forms._
import play.api.data._
import play.api.db.slick._
import play.api.mvc._
import play.api.Play.current

import play.api.libs.json._


object JsonTweetController extends Controller with AuthElement with AuthConfigImpl {

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

  implicit def tuple2[A : Writes, B : Writes]: Writes[(A, B)] = new Writes[(A, B)] {
    def writes(o: (A, B)): JsValue = Json.obj("member1" -> o._1, "member2" -> o._2)
  }

  /**
   * 一覧表示(自分のみ)
   */
  def list(kind: String) = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    DB.withSession { implicit session =>
      val tweets = if (kind == "all") {
        all(loggedIn.id)
      } else {
        myself(loggedIn.id)
      }
      Ok(Json.toJson(tweets))
    }
  }

  /**
   * 一覧表示(自分のみ)
   */
  def myself(id: Int) = {
    DB.withSession { implicit session =>
      Tweet
        .filter(_.userId === id)
        .innerJoin(TwiUser).on { (t, u) =>
          t.userId === u.id
        }
        .map { case (t, u) =>
          (u.name, t)
        }
        .sortBy(_._2.insTime.desc)
        .list
    }
  }

  /**
   * 一覧表示(フォロワー含む)
   */
  def all(id: Int) = {
    DB.withSession { implicit session =>
      Tweet
        .innerJoin(TwiUser).on { (t, u) =>
          t.userId === u.id
        }
        .filter { case (t, u) =>
          (t.userId in
            Follow
              .filter(_.userId === id)
              .map(_.followUserId)
            ) || (t.userId === id)
        }
        .map { case (t, u) =>
          (u.name, t)
        }
        .sortBy(_._2.insTime.desc)
        .list
    }
  }

  /**
   * Tweet用JSON API
   */
  def create = StackAction (parse.json, AuthorityKey -> NormalUser) { implicit request =>
    request.body.validate[TweetForm].map { form =>
      DB.withSession { implicit session =>
        val tweet = TweetRow(0, loggedIn.id, form.content, null, null)
        Tweet.insert(tweet)
        Ok(Json.obj("result" -> "success"))
      }
    }.recoverTotal { e =>
      BadRequest(Json.obj("result" -> "failure", "error" -> JsError.toFlatJson(e)))
    }
  }
}
