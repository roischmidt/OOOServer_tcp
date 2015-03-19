package oooserver.server

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.util.Timeout
import com.redis.RedisClient
import oooserver.server.api.{CustomErrorException, ErrorCode}
import org.slf4j.LoggerFactory
import play.api.libs.json.{JsSuccess, Json}

import scala.concurrent.Future
import scala.concurrent.duration._
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

    // clear all DB
    def clearAll() = client.flushdb()

    // store CacheData object
    def store(key: String, value: CacheData): Future[Boolean] =
        storeEx(key, expired, value)

    // store CacheData object with provided expiration
    def storeEx(key: String, expire: Int, value: CacheData): Future[Boolean] =
        client.setex(key, expire, Json.toJson(value).toString())

    // renew expiration for a specific key
    def resume(key: String, expiration: Int = expired): Future[Boolean] =
        client.expire(key, expiration)

    // check if key exists
    def exists(key: String): Future[Boolean] =
        client.exists(key)

    // get CacheData object
    def get(username: String): Future[Option[CacheData]] =
        client.get(username).map {
            case Some(x) => CacheData.fmtJson.reads(Json.parse(x.asInstanceOf[String])) match {
                case JsSuccess(d, _) => Some(d)
                case _ => throw CustomErrorException("Couldn't parse cached data", ErrorCode.ERR_SYSTEM)
            }
            case None => None
        }

    // check if a user is already paired to another
    def isPaired(username: String): Future[Boolean] =
        get(username).map { cdOpt =>
            cdOpt.exists(cd => cd.opponent.isDefined)
        }

    // returns the online player list
    def onlinePlayers(): Future[List[String]] = {
        client.keys()
    }

    // returns the free player list
    def freePlayerList(): Future[List[String]] =
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

    // find random free player
    def findFreePlayer(): Future[String] =
        freePlayerList.map { pp =>
            pp.head
        }

    // set an opponent to a specific player
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

    // pair 2 players
    def pairWith(op1: String, op2: String): Future[Boolean] =
        isPaired(op1).flatMap {
            case true => throw CustomErrorException(s"$op1 is already paired", ErrorCode.ERR_USER_ALREADY_PAIRED)
            case false => isPaired(op2).flatMap {
                case true => throw CustomErrorException(s"$op2 is already paired", ErrorCode.ERR_USER_ALREADY_PAIRED)
                case false =>
                    setOpponent(op1, op2).flatMap {
                        case true => setOpponent(op2, op1)
                        case false => Future.successful(false)
                    }
            }

        }

    // pair a player with random oppnent
    def pairAnonymous(username: String): Future[String] =
        isPaired(username).flatMap {
            case true => throw new Throwable(s"$username is already paired")
            case false =>
                findFreePlayer.flatMap { fp =>
                    pairWith(username, fp).map {
                        case true => fp
                        case false => throw CustomErrorException("Problem with pair", ErrorCode.ERR_USER_ALREADY_PAIRED)
                    }.recoverWith {
                        case e: Throwable => throw e
                    }
                }
        }

    // unpair a player from his opponent
    def unpairPlayer(username: String): Future[Option[String]] =
        get(username).flatMap { cdOp =>
            cdOp.isDefined match {
                case true => store(username, cdOp.get.copy(opponent = None)).flatMap {
                    case true =>
                        cdOp.get.opponent.isDefined match {
                            case true =>
                                get(cdOp.get.opponent.get).flatMap { cdOp2 =>
                                    cdOp2.isDefined match {
                                        case true => store(cdOp.get.opponent.get, cdOp2.get.copy(opponent = None)).map { _ => cdOp.get.opponent }
                                        case false => Future.successful(cdOp.get.opponent)
                                    }
                                }
                            case false => Future.successful(None)
                        }

                    case false => throw CustomErrorException("Unpair error", ErrorCode.ERR_SYSTEM)
                }
                case false => throw CustomErrorException(s"$username not found", ErrorCode.ERR_USER_OFFLINE)
            }
        }

    // get memory data from a player's CacheData object
    def getFromMemory(username: String, key: String): Future[Option[String]] =
        get(username).map { cdOp =>
            cdOp.flatMap { cd =>
                cd.memory.flatMap { mem =>
                    mem.get(key)
                }
            }
        }

}
