package controllers

import java.sql.Timestamp

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

object JsonTweetController extends Controller with AuthElement with AuthConfigImpl {

  // フォームの値を格納する
  case class TweetForm(content: String, rootId: Int)

  // formのデータとケースクラスの変換を行う
  val tweetForm = Form(
    mapping(
      "content" -> nonEmptyText(maxLength = 140),
      "rootId" -> number
    )(TweetForm.apply)(TweetForm.unapply)
  )

  implicit val tweetFormReads = Json.reads[TweetForm]
  implicit val tweetRowWriter = Json.writes[TweetRow]

  implicit def tuple2[A : Writes, B : Writes]: Writes[(A, B)] = new Writes[(A, B)] {
    def writes(o: (A, B)): JsValue = Json.obj("member1" -> o._1, "member2" -> o._2)
  }

  implicit def tuple3[A : Writes, B : Writes, C: Writes]: Writes[(A, B, C)] = new Writes[(A, B, C)] {
    def writes(o: (A, B, C)): JsValue = Json.obj("member1" -> o._1, "member2" -> o._2, "member3" -> o._3)
  }

  implicit def tuple4[A : Writes, B : Writes, C: Writes, D:Writes]: Writes[(A, B, C, D)] = new Writes[(A, B, C, D)] {
    def writes(o: (A, B, C, D)): JsValue = Json.obj("member1" -> o._1, "member2" -> o._2, "member3" -> o._3, "member4" -> o._4)
  }

  implicit def tuple5[A : Writes, B : Writes, C: Writes, D:Writes, E:Writes]: Writes[(A, B, C, D, E)] = new Writes[(A, B, C, D, E)] {
    def writes(o: (A, B, C, D, E)): JsValue = Json.obj("member1" -> o._1, "member2" -> o._2, "member3" -> o._3, "member4" -> o._4, "member5" -> o._5)
  }

  /**
   * 一覧表示(自分のみ)
   */
  def list(kind: String) = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    val tweets = if (kind == "all") {
//      self(kind)
      all(loggedIn.id)
    } else {
      all(loggedIn.id)
//      self(kind)
    }
    Ok(Json.toJson(tweets))
  }

  /**
   * 一覧表示(関連)
   */
  def relation(id: Int) = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    val tweets = DB.withSession { implicit session =>
      Tweet
        .filter(t => t.id === id || t.rootId === id)
        .innerJoin(TwiUser).on { (t, u) =>
          t.userId === u.id
        }
        .map { case (t, u) =>
          (u.name, t)
        }
        .sortBy(_._2.insTime.asc)
        .list
      }

    Ok(Json.toJson(tweets))
  }

  /**
   * 一覧表示(対象者のみ)
   */
  def self(name: String) = {
    DB.withSession { implicit session =>
      TwiUser.filter(_.name === name).firstOption.map { user =>
        Tweet
          .filter { t =>
            (t.userId === user.id) || (t.id in ReTweet.filter(_.userId === user.id).map(_.tweetId))
          }
          .leftJoin(ReTweet).on { (t, rt) =>
            t.id === rt.tweetId && rt.userId === user.id
          }
          .innerJoin(TwiUser).on { case ((t, rt), u) =>
            t.userId === u.id
          }
          .map { case ((t, rt), u) =>
            (u.name, t, rt.insTime.?.getOrElse(new Timestamp(0)))
          }
          .list
          .sortWith { (a, b) =>
            val aTime = if (a._3.getTime > a._2.insTime.getTime) {
              a._3
            } else {
              a._2.insTime
            }
            val bTime = if (b._3.getTime > a._2.insTime.getTime) {
              b._3
            } else {
              b._2.insTime
            }
            aTime.getTime > bTime.getTime
          }
      }.getOrElse {
        List()
      }
    }
  }

//  /**
//   * 一覧表示(フォロワー含む)
//   */
//  def all(id: Int) = {
//    DB.withSession { implicit session =>
//      Tweet
//        .leftJoin(Tweet).on { (t1, t2) =>
//          t1.id === t2.rtId
//        }
//        .innerJoin(TwiUser).on { case ((t1, t2), u) =>
//          t1.userId === u.id
//        }
//        .filter { case ((t1, t2), u) =>
//          (t1.userId === id && t1.rtId === 0) || (t1.userId in Follow.filter(_.userId === id).map(_.followUserId)) && (t1.rtId === 0)
//        }
//        .map { case ((t1, t2), u) =>
////        (u.name, t1, t2.insTime.?.getOrElse(t1.insTime), t1.userId === id || t2.userId.? === id)
//          (u.name, t1, t2.insTime.?.getOrElse(new Timestamp(0)), t1.userId === id || t2.userId.? === id)
//        }
////        .sortBy { case (nm, t1, time, rtFlag) =>
////          time.desc
////        }
//        .list
//        .sortWith { (a, b) =>
//          val aTime = if (a._3.getTime > a._2.insTime.getTime) {
//            a._3
//          } else {
//            a._2.insTime
//          }
//          val bTime = if (b._3.getTime > a._2.insTime.getTime) {
//            b._3
//          } else {
//            b._2.insTime
//          }
//          aTime.getTime > bTime.getTime
//        }
//        .map { case (nm, t1, time, rtFlag) =>
//          val list =
//            Tweet
//              .filterNot { t => t.rtId === 0}
//              .filter { t => t.rtId === t1.id }
//              .innerJoin(TwiUser).on { (t, u) =>
//                t.userId === u.id
//              }
//              .map { case (t, u) =>
//                u.name
//              }
//              .list
//          (nm, t1, list, rtFlag)
//        }
//      }
//  }

  /**
   * 一覧表示(フォロワー含む)
   */
  def all(id: Int) = {
    DB.withSession { implicit session =>
      val tweets =
        Tweet
          .filter {t =>
            t.userId === id || (t.userId in Follow.filter(f => f.userId === id && f.flag).map(_.followUserId))
          }
          .leftJoin(Tweet).on { (t1, t2) =>
            t1.rtId === t2.id
          }
          .innerJoin(TwiUser).on { case ((t1, t2), u1) =>
            t1.userId === u1.id
          }
          .leftJoin(TwiUser).on { case (((t1, t2), u1), u2) =>
            t2.userId.? === u2.id
          }
          .map { case (((t1, t2), u1), u2) =>
            (u1.name, t1, u2.name.?, t2.?, t1.userId === id || t2.userId.? === id)
          }
          .sortBy { case (nm1, t1, n2, t2, flag) =>
            t1.insTime.desc
          }
          .list
      tweets
        .map { case (nm1, t1, nm2, t2, rtFlag) =>
          val list =
            Tweet
              .filterNot { t => t.rtId === 0}
              .filter { t => t.rtId === t1.id }
              .innerJoin(TwiUser).on { (t, u) =>
                t.userId === u.id
              }
              .map { case (t, u) =>
                u.name
              }
              .list
//        (nm1, t1, nm2, t2, list, rtFlag)
          (nm2.getOrElse(nm1), t2.getOrElse(t1), t1.insTime, list, rtFlag)
        }
        .distinct
    }
  }

  /**
   * Tweet用JSON API
   */
  def create = StackAction (parse.json, AuthorityKey -> NormalUser) { implicit request =>
    request.body.validate[TweetForm].map { form =>
      DB.withSession { implicit session =>
        val tweet = TweetRow(0, loggedIn.id, Some(form.content), form.rootId, 0, null, null)
        Tweet.insert(tweet)
        Ok(Json.obj("result" -> "success"))
      }
    }.recoverTotal { e =>
      BadRequest(Json.obj("result" -> "failure", "error" -> JsError.toFlatJson(e)))
    }
  }
}
