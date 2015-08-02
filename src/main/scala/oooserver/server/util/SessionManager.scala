package oooserver.server.util

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem}
import akka.util.Timeout
import com.redis.RedisClient
import com.typesafe.config.ConfigFactory
import oooserver.server.api.{ErrorCode, CustomErrorException}
import org.slf4j.LoggerFactory
import play.api.libs.json.{JsSuccess, Json}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

case class UserData(
        opponent: Option[String],
        memory: Option[Map[String, String]] // any data that needs to be saved
        )

object UserData {
    implicit val fmtJson = Json.format[UserData]
}

object SessionManager {

    final val logger = LoggerFactory.getLogger(this.getClass)

    var sessions : Map[ActorRef,String] = Map() // map(socketRef,username)

    final val expired = TimeUnit.SECONDS.convert(5, TimeUnit.MINUTES).toInt // session expires after 5 minutes of inactivity

    // Akka setup
    implicit val system = ActorSystem("redis-client")
    implicit val executionContext = system.dispatcher
    implicit val timeout = Timeout(5 seconds)

    // Redis client setup
    val client = RedisClient(ConfigFactory.load.getString("OOOServer.redis.host"), ConfigFactory.load.getInt("OOOServer.redis.port"))

    // clear all DB
    def clearAll() : Future[Unit] = {
        sessions = sessions.empty
        client.flushdb().map{_ => }
    }

    // store CacheData object
    def store(key: String, value: UserData): Future[Boolean] =
        storeEx(key, expired, value)

    // store CacheData object with provided expiration
    def storeEx(key: String, expire: Int, value: UserData): Future[Boolean] =
        client.setex(key, expire, Json.toJson(value).toString())

    // renew expiration for a specific key
    def resume(key: String, expiration: Int = expired): Future[Boolean] =
        client.expire(key, expiration)

    // check if key exists
    def exists(key: String): Future[Boolean] =
        client.exists(key)

    // remove key
    def remove(key: String): Future[Boolean] =
        client.del(key).map{n => n > 0}

    // check if user is online
    def isOnline(nickname: String): Future[Boolean] = exists(nickname)

    // adds a new user
    def addUser(nickname: String, sessionRef: ActorRef) : Future[Boolean] =
        isOnline(nickname).flatMap {
            case true =>
                logger.info(s"$nickname is already online")
                Future.successful(false)
            case false =>
                store(nickname,UserData(None,None)).map {
                    case true =>
                        sessions = sessions.+(sessionRef -> nickname)
                        logger.info(s"number of online users now ${sessions.size}")
                        true
                    case false =>
                        logger.error("Couldn't store user in redis")
                        throw CustomErrorException(s"Couldn't add user to DB",ErrorCode.ERR_SYSTEM)

                }
        }


    def removeUserBySessionRef(sessionRef: ActorRef) : Future[Boolean] =
        sessions.find(_._1 == sessionRef).map { e =>
            remove(e._2).map{
                case true =>
                    sessions = sessions.filterNot(_._1 == sessionRef)
                    logger.info(s"number of online users now ${sessions.size}")
                    true
                case false => false
            }

        }.getOrElse(Future.successful(false))

    def removeUserByNickname(nickname: String) : Future[Boolean] =
            remove(nickname).map{
                case true =>
                    sessions = sessions.filterNot(_._2 == nickname)
                    logger.info(s"number of online users now ${sessions.size}")
                    true
                case false => false
            }

    // get the user sessionRef
    def getUserSessionRef(nickname: String) : Option[ActorRef] = sessions.find(_._2 == nickname).map{e => e._1}

    // get UserData object
    def getData(username: String): Future[Option[UserData]] =
        client.get(username).map(_.map(userData =>
            UserData.fmtJson.reads(Json.parse(userData.asInstanceOf[String])) match {
                case JsSuccess(dataObj, _) => Some(dataObj)
                case _ => None
            }).getOrElse(None))

    // check if a user is already paired to another
    def isPaired(username: String): Future[Boolean] =
        getData(username).map(_.exists(_.opponent.isDefined))

    // returns the online player list
    def onlinePlayers(): List[String] =
        sessions.valuesIterator.toList

    // returns the free player list
    def freePlayerList(): Future[List[String]] =
          Future.sequence( onlinePlayers.map { username =>
              isPaired(username).map {
                  case true => ""
                  case false => username
              }
          }).map(_.filter(_.nonEmpty))

    // find random free player
    def findFreePlayer(usernameToExclude: Option[String] = None): Future[Option[String]] =
        freePlayerList().map {
            _.filterNot(usernameToExclude.contains(_)) match {
                case ls@x::Nil => Some(ls.head)
                case Nil => None
            }
        }

    // set an opponent to a specific player
    def setOpponent(username: String, opName: String): Future[Boolean] =
        getData(username).flatMap (_.map { d =>
            d.opponent.isDefined match {
                case true =>
                    logger.info(s"Can't Pair $username with $opName because $opName is already paired with ${d.opponent.get}")
                    Future.successful(false)
                case false =>
                    logger.info(s"Pairing $username with $opName")
                    store(username, d.copy(opponent = Some(opName)))
            }
        }.getOrElse(Future.successful(false)))

    //get the opponent name if exists
    def getOpponentName(username: String) : Future[Option[String]] =
        getData(username).map(_.map(_.opponent).getOrElse(None))

    // pair 2 players
    def pairWith(op1: String, op2: String): Future[Boolean] =
      for {
          p1 <- setOpponent(op1, op2)
          p2 <- setOpponent(op2, op1)
      } yield (p1,p2) match {
          case (true,true) =>
              true
          case _ =>
              logger.info("One of the players is already paired")
              false
      }

    // pair a player with random oppnent
    def pairAnonymous(username: String): Future[Option[String]] =
        findFreePlayer(Some(username)).flatMap{ fpOpt =>
           fpOpt.map { fp =>
               pairWith(username,fp).map {
                   case true => fpOpt
                   case false =>
                       logger.info(s"couldn't pair $username with $fp")
                       None
               }
           }.getOrElse(Future.successful(None))
        }

    // unpair a player from his opponent
    def unpairPlayer(username: String): Future[Option[String]] =
        getData(username).flatMap {
            case Some(ud) => ud.opponent.map { opName =>
                store(username,ud.copy(opponent = None)).flatMap(_ =>
                    getData(opName).flatMap(_.map{opData =>
                        store(opName,opData.copy(opponent = None)).map{_ => Some(opName)}
                    }.getOrElse(Future.successful(None))))
            }.getOrElse(Future.successful(None))

            case None =>
                logger.info(s"$username not found")
                Future.successful(None)
        }

    // get memory data from a player's CacheData object
    def getFromMemory(username: String, key: String): Future[Option[String]] =
        getData(username).map {
            _.map {
                _.memory.get(key)
            }
        }
}
