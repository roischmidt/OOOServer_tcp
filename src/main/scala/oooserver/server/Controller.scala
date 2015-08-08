package oooserver.server

import akka.actor.{Actor, ActorRef}
import akka.io.Tcp
import akka.util.ByteString
import oooserver.server.api._
import oooserver.server.handlers.{LogoutHandler, LoginHandler}
import oooserver.server.util.SessionManager
import org.slf4j.LoggerFactory
import play.api.libs.json.{JsError, JsSuccess, Json}

import scala.concurrent.ExecutionContext.Implicits.global


/**
 * Created by rois on 6/20/15.
 */

class Controller extends Actor {

    import Tcp._

    final val logger = LoggerFactory.getLogger(this.getClass)
    var senderA: ActorRef = null

    def receive = {

        case Received(data) =>
            logger.info(s"Controller Received ${data.utf8String} , sender: ${sender}")
            Message.fmtJsonReads.reads(Json.parse({
                data.utf8String
            })) match {
                case JsSuccess(msg, _) => msg match {
                    case m: LoginRequest =>
                        logger.info("login request arrived")
                        LoginHandler.handle(m, sender()).map { loginRes =>
                            SessionManager.getUserSessionRef(m.nickname).foreach { ref =>
                                ref ! Write(ByteString(LoginResponse.fmtJson.writes(loginRes).toString()))
                            }
                        }
                    case m: LogoutRequest =>
                        logger.info("logout request arrived")
                        LogoutHandler.handle(m,sender()).map { logoutRes =>
                                sender ! Write(ByteString(LogoutResponse.fmtJson.writes(logoutRes).toString()))
                            }
                    case _ =>
                        logger.warn(s"unhandled message arrived $msg")
                }
                case JsError(e) =>
                    logger.warn("unsupported message arrived")
            }


        case PeerClosed =>
            logger.info(s"client disconnected, sender: $sender")
            // TODO: handle the user removal in logout handler
            SessionManager.removeUserBySessionRef(sender).andThen {
                case _ => context stop self
            }

    }
}
