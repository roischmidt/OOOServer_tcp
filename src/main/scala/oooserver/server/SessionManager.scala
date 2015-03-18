package oooserver.server

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.util.Timeout
import com.redis.RedisClient
import oooserver.server.api.{ErrorCode, CustomErrorException}
import org.slf4j.LoggerFactory
import play.api.libs.json.{JsSuccess, Json}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

case class CacheData(
                      sessionId: String,
                      opponent: Option[String],
                      memory: Option[Map[String, String]]
                      )

object CacheData {
  implicit val fmtJson = Json.format[CacheData]
}

object SessionManager {

  final val logger = LoggerFactory.getLogger(this.getClass)

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
        case _ => throw CustomErrorException("Couldn't parse cached data",ErrorCode.ERR_SYSTEM)
      }
      case None => None
    }

  def isPaired(username: String): Future[Boolean] =
    get(username).map { cdOpt =>
      cdOpt.exists(cd => cd.opponent.isDefined)
    }


  def onlinePlayers(): Future[List[String]] = {
    client.keys()
  }

  def unPairedPlayers(): Future[List[String]] =
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


  def findFreePlayer(): Future[String] =
    unPairedPlayers.map { pp =>
      pp.head
    }

  def setOpponent(username: String, opName: String): Future[Boolean] =
    get(username).flatMap { cdOp =>
      cdOp.map { cd =>
        cd.opponent.isDefined match {
          case true =>
            logger.info(s"Can't Pair $username with $opName because $opName is alredy paired with ${cd.opponent.get}")
            Future.successful(false)
          case false =>
            logger.info(s"Pairing $username with $opName")
            store(username, cd.copy(opponent = Some(opName)))
        }
      }.getOrElse(Future.successful(false))

    }

  def pairWith(op1: String, op2: String): Future[Boolean] =
    isPaired(op1).flatMap{
      case true => throw CustomErrorException(s"$op1 is already paired",ErrorCode.ERR_USER_ALREADY_PAIRED)
      case false => isPaired(op2).flatMap{
        case true => throw CustomErrorException(s"$op2 is already paired",ErrorCode.ERR_USER_ALREADY_PAIRED)
        case false =>
          setOpponent(op1, op2).flatMap {
            case true => setOpponent(op2, op1)
            case false => Future.successful(false)
          }
      }

    }

  def pairAnonymous(username: String): Future[String] =
    isPaired(username).flatMap{
      case true => throw new Throwable(s"$username is already paired")
      case false =>
      findFreePlayer.flatMap { fp =>
        pairWith(username, fp).map {
          case true => fp
          case false => throw CustomErrorException("Problem with pair",ErrorCode.ERR_USER_ALREADY_PAIRED)
        }
      }
    }

  def getFromMemory(username: String,key: String): Future[Option[String]] =
    get(username).map { cdOp =>
        cdOp.flatMap{cd =>
          cd.memory.flatMap{mem =>
            mem.get(key)
          }
        }
      }

}
