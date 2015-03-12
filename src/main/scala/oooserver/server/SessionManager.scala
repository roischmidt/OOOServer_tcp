package oooserver.server

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.util.Timeout
import com.redis.RedisClient
import play.api.libs.json.{JsSuccess, Json}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

case class CacheData(
                      token: String,
                      opponent: Option[String],
                      memory: Option[Map[String, String]]
                      )

object CacheData {
  implicit val fmtJson = Json.format[CacheData]
}

object SessionManager {

  final val expired = TimeUnit.SECONDS.convert(5, TimeUnit.MINUTES).toInt // session expires after 5 minutes of inactivity

  // Akka setup
  implicit val system = ActorSystem("redis-client")
  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(5 seconds)

  // Redis client setup
  val client = RedisClient("localhost", 6379)

  def clearAll() = client.flushdb()

  def store(key: String, value: CacheData): Future[Boolean] =
    storeEx(key, expired, value)

  def storeEx(key: String, expire: Int, value: CacheData): Future[Boolean] =
    client.setex(key, expire, Json.toJson(value).toString())

  def resume(key: String, expiration: Int = expired): Future[Boolean] =
    client.expire(key, expiration)

  def exists(key: String): Future[Boolean] =
    client.exists(key)

  def get(username: String): Future[Option[CacheData]] =
    client.get(username).map {
      case Some(x) => CacheData.fmtJson.reads(Json.parse(x.asInstanceOf[String])) match {
        case JsSuccess(d, _) => Some(d)
        case _ => throw new IllegalStateException("Couldn't parse cached data")
      }
      case None => None
    }

  def isPaired(username: String): Future[Boolean] =
    get(username).map { cdOpt =>
      cdOpt.exists(cd => cd.opponent.isDefined)
    }

  def onlinePlayersSorted(): Future[List[String]] = {
    client.keys().map(e => e.sorted)
  }

  def onlinePlayers(): Future[List[String]] = {
    client.keys()
  }

  def unPairedPlayers: Future[List[String]] =
    onlinePlayers().flatMap { op =>
      Future.sequence(op.map {
        case username =>
          isPaired(username).map {
            case true => ""
            case false => username
          }
      }).map {
        res => res.filter(op => op.nonEmpty)
      }
    }


  def findFreePlayer: Future[String] =
    unPairedPlayers.map { pp =>
      pp.head
    }

  def setOpponent(username: String, opName: String): Future[Boolean] =
    get(username).flatMap { cdOp =>
      cdOp.map { cd =>
        cd.opponent.isDefined match {
          case true => Future.successful(false)
          case false => store(username, cd.copy(opponent = Some(opName)))
        }
      }.getOrElse(Future.successful(false))

    }

  def pairWith(op1: String, op2: String): Future[Boolean] =
    setOpponent(op1, op2).flatMap {
      case true => setOpponent(op2, op1)
      case false => Future.successful(false)
    }

  def pairAnonimous(username: String): Future[String] =
    findFreePlayer.flatMap { fp =>
      pairWith(username, fp).map {
        case true => fp
        case false => throw new Throwable("Problem with pair")
      }
    }

  /*
    returns opponent2
   */
  //  def pairAvailable(opponent_1: String): Option[String] = {
  //    onlinePlayers().map { ls =>
  //      ls.foreach { e =>
  //        SessionManager.get(e).map { cdOp =>
  //          cdOp.map { cd =>
  //            if (!cd.opponent.isDefined) {
  //              //todo : pair players
  //            }
  //          }
  //        }
  //      }
  //    }


  def main(args: Array[String]) {
    val c = s"""{"token":"123456","memory":{"a":"aa","b":"bb"}}"""
    println(CacheData.fmtJson.reads(Json.parse(c)))

    SessionManager.store("a", CacheData.fmtJson.reads(Json.parse(c)).get)
    SessionManager.store("b", CacheData.fmtJson.reads(Json.parse(c)).get)
    SessionManager.store("c", CacheData("", Some("b"), None))
    SessionManager.store("d", CacheData.fmtJson.reads(Json.parse(c)).get)
    SessionManager.store("e", CacheData.fmtJson.reads(Json.parse(c)).get)
    SessionManager.store("avv", CacheData("", Some("d"), None))
    SessionManager.store("adds", CacheData.fmtJson.reads(Json.parse(c)).get)
    SessionManager.store("arrr", CacheData.fmtJson.reads(Json.parse(c)).get)
    println(Await.result(SessionManager.onlinePlayers(), Duration("5 second")))
    println(Await.result(SessionManager.isPaired("arrr"), Duration("5 second")))
    println(Await.result(SessionManager.unPairedPlayers, Duration("5 second")))
    println(Await.result(SessionManager.findFreePlayer, Duration("5 second")))
  }

}
