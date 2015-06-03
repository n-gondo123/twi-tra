package models
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = scala.slick.driver.MySQLDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: scala.slick.driver.JdbcProfile
  import profile.simple._
  import scala.slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import scala.slick.jdbc.{GetResult => GR}
  
  /** DDL for all tables. Call .create to execute. */
  lazy val ddl = Follow.ddl ++ Tweet.ddl ++ TwiUser.ddl
  
  /** Entity class storing rows of table Follow
   *  @param id Database column ID DBType(INT), AutoInc, PrimaryKey
   *  @param userId Database column USER_ID DBType(INT)
   *  @param followUserId Database column FOLLOW_USER_ID DBType(INT)
   *  @param flag Database column FLAG DBType(BIT)
   *  @param insTime Database column INS_TIME DBType(TIMESTAMP)
   *  @param updTime Database column UPD_TIME DBType(TIMESTAMP) */
  case class FollowRow(id: Int, userId: Int, followUserId: Int, flag: Boolean, insTime: java.sql.Timestamp, updTime: java.sql.Timestamp)
  /** GetResult implicit for fetching FollowRow objects using plain SQL queries */
  implicit def GetResultFollowRow(implicit e0: GR[Int], e1: GR[Boolean], e2: GR[java.sql.Timestamp]): GR[FollowRow] = GR{
    prs => import prs._
    FollowRow.tupled((<<[Int], <<[Int], <<[Int], <<[Boolean], <<[java.sql.Timestamp], <<[java.sql.Timestamp]))
  }
  /** Table description of table FOLLOW. Objects of this class serve as prototypes for rows in queries. */
  class Follow(_tableTag: Tag) extends Table[FollowRow](_tableTag, "FOLLOW") {
    def * = (id, userId, followUserId, flag, insTime, updTime) <> (FollowRow.tupled, FollowRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, userId.?, followUserId.?, flag.?, insTime.?, updTime.?).shaped.<>({r=>import r._; _1.map(_=> FollowRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID DBType(INT), AutoInc, PrimaryKey */
    val id: Column[Int] = column[Int]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column USER_ID DBType(INT) */
    val userId: Column[Int] = column[Int]("USER_ID")
    /** Database column FOLLOW_USER_ID DBType(INT) */
    val followUserId: Column[Int] = column[Int]("FOLLOW_USER_ID")
    /** Database column FLAG DBType(BIT) */
    val flag: Column[Boolean] = column[Boolean]("FLAG")
    /** Database column INS_TIME DBType(TIMESTAMP) */
    val insTime: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("INS_TIME")
    /** Database column UPD_TIME DBType(TIMESTAMP) */
    val updTime: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("UPD_TIME")
    
    /** Index over (followUserId) (database name FOLLOW_USER_ID) */
    val index1 = index("FOLLOW_USER_ID", followUserId)
    /** Index over (userId) (database name USER_ID) */
    val index2 = index("USER_ID", userId)
  }
  /** Collection-like TableQuery object for table Follow */
  lazy val Follow = new TableQuery(tag => new Follow(tag))
  
  /** Entity class storing rows of table Tweet
   *  @param id Database column ID DBType(INT), AutoInc, PrimaryKey
   *  @param userId Database column USER_ID DBType(INT)
   *  @param content Database column CONTENT DBType(VARCHAR), Length(140,true), Default(None)
   *  @param rootId Database column ROOT_ID DBType(INT)
   *  @param rtId Database column RT_ID DBType(INT), Default(0)
   *  @param insTime Database column INS_TIME DBType(TIMESTAMP)
   *  @param updTime Database column UPD_TIME DBType(TIMESTAMP) */
  case class TweetRow(id: Int, userId: Int, content: Option[String] = None, rootId: Int, rtId: Int = 0, insTime: java.sql.Timestamp, updTime: java.sql.Timestamp)
  /** GetResult implicit for fetching TweetRow objects using plain SQL queries */
  implicit def GetResultTweetRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[java.sql.Timestamp]): GR[TweetRow] = GR{
    prs => import prs._
    TweetRow.tupled((<<[Int], <<[Int], <<?[String], <<[Int], <<[Int], <<[java.sql.Timestamp], <<[java.sql.Timestamp]))
  }
  /** Table description of table tweet. Objects of this class serve as prototypes for rows in queries. */
  class Tweet(_tableTag: Tag) extends Table[TweetRow](_tableTag, "tweet") {
    def * = (id, userId, content, rootId, rtId, insTime, updTime) <> (TweetRow.tupled, TweetRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, userId.?, content, rootId.?, rtId.?, insTime.?, updTime.?).shaped.<>({r=>import r._; _1.map(_=> TweetRow.tupled((_1.get, _2.get, _3, _4.get, _5.get, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID DBType(INT), AutoInc, PrimaryKey */
    val id: Column[Int] = column[Int]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column USER_ID DBType(INT) */
    val userId: Column[Int] = column[Int]("USER_ID")
    /** Database column CONTENT DBType(VARCHAR), Length(140,true), Default(None) */
    val content: Column[Option[String]] = column[Option[String]]("CONTENT", O.Length(140,varying=true), O.Default(None))
    /** Database column ROOT_ID DBType(INT) */
    val rootId: Column[Int] = column[Int]("ROOT_ID")
    /** Database column RT_ID DBType(INT), Default(0) */
    val rtId: Column[Int] = column[Int]("RT_ID", O.Default(0))
    /** Database column INS_TIME DBType(TIMESTAMP) */
    val insTime: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("INS_TIME")
    /** Database column UPD_TIME DBType(TIMESTAMP) */
    val updTime: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("UPD_TIME")
    
    /** Index over (userId) (database name USER_ID) */
    val index1 = index("USER_ID", userId)
  }
  /** Collection-like TableQuery object for table Tweet */
  lazy val Tweet = new TableQuery(tag => new Tweet(tag))
  
  /** Entity class storing rows of table TwiUser
   *  @param id Database column ID DBType(INT), AutoInc, PrimaryKey
   *  @param name Database column NAME DBType(VARCHAR), Length(20,true)
   *  @param email Database column EMAIL DBType(VARCHAR), Length(100,true)
   *  @param password Database column password DBType(VARCHAR), Length(255,true)
   *  @param insTime Database column INS_TIME DBType(TIMESTAMP)
   *  @param updTime Database column UPD_TIME DBType(TIMESTAMP) */
  case class TwiUserRow(id: Int, name: String, email: String, password: String, insTime: java.sql.Timestamp, updTime: java.sql.Timestamp)
  /** GetResult implicit for fetching TwiUserRow objects using plain SQL queries */
  implicit def GetResultTwiUserRow(implicit e0: GR[Int], e1: GR[String], e2: GR[java.sql.Timestamp]): GR[TwiUserRow] = GR{
    prs => import prs._
    TwiUserRow.tupled((<<[Int], <<[String], <<[String], <<[String], <<[java.sql.Timestamp], <<[java.sql.Timestamp]))
  }
  /** Table description of table twi_user. Objects of this class serve as prototypes for rows in queries. */
  class TwiUser(_tableTag: Tag) extends Table[TwiUserRow](_tableTag, "twi_user") {
    def * = (id, name, email, password, insTime, updTime) <> (TwiUserRow.tupled, TwiUserRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, email.?, password.?, insTime.?, updTime.?).shaped.<>({r=>import r._; _1.map(_=> TwiUserRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID DBType(INT), AutoInc, PrimaryKey */
    val id: Column[Int] = column[Int]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column NAME DBType(VARCHAR), Length(20,true) */
    val name: Column[String] = column[String]("NAME", O.Length(20,varying=true))
    /** Database column EMAIL DBType(VARCHAR), Length(100,true) */
    val email: Column[String] = column[String]("EMAIL", O.Length(100,varying=true))
    /** Database column password DBType(VARCHAR), Length(255,true) */
    val password: Column[String] = column[String]("password", O.Length(255,varying=true))
    /** Database column INS_TIME DBType(TIMESTAMP) */
    val insTime: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("INS_TIME")
    /** Database column UPD_TIME DBType(TIMESTAMP) */
    val updTime: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("UPD_TIME")
    
    /** Uniqueness Index over (email) (database name EMAIL) */
    val index1 = index("EMAIL", email, unique=true)
    /** Uniqueness Index over (name) (database name NAME) */
    val index2 = index("NAME", name, unique=true)
  }
  /** Collection-like TableQuery object for table TwiUser */
  lazy val TwiUser = new TableQuery(tag => new TwiUser(tag))
}