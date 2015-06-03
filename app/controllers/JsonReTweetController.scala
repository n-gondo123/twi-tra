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


object JsonReTweetController extends Controller with AuthElement with AuthConfigImpl {

  // フォームの値を格納する
  case class ReTweetForm(reTweetId: Int)

  // formのデータとケースクラスの変換を行う
  val reTweetForm = Form(
    mapping(
      "reTweetId" -> number
    )(ReTweetForm.apply)(ReTweetForm.unapply)
  )

  implicit val reTweetFromReads = Json.reads[ReTweetForm]
  implicit val reTweetRowWriter = Json.writes[TweetRow]

  /**
   * Tweet用JSON API
   */
  def create = StackAction (parse.json, AuthorityKey -> NormalUser) { implicit request =>
    request.body.validate[ReTweetForm].map { form =>
      DB.withSession { implicit session =>
        val tweet = TweetRow(0, loggedIn.id, null, 0, form.reTweetId, null, null)
        Tweet.insert(tweet)
        Ok(Json.obj("result" -> "success"))
      }
    }.recoverTotal { e =>
      BadRequest(Json.obj("result" -> "failure", "error" -> JsError.toFlatJson(e)))
    }
  }
}
