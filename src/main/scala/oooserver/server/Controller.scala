package oooserver.server

import akka.actor.{ActorRef, Actor}
import akka.io.Tcp
import oooserver.server.api._
import oooserver.server.handlers.LoginHandler
import org.slf4j.LoggerFactory
import play.api.libs.json.{JsError, JsSuccess, Json}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
 * Created by rois on 6/20/15.
 */

class Controller extends Actor {

    import Tcp._

    final val logger = LoggerFactory.getLogger(this.getClass)
    var senderA : ActorRef = null

    def receive = {
        case Received(data) =>
            logger.info(s"Controller Received ${data.utf8String}")
            Message.fmtJsonReads.reads(Json.parse({data.utf8String})) match {
                case JsSuccess(msg,_) => msg match {
                   // case m : LoginRequest => println("login request arrived")
                    case _ =>

                        println(s"unhandled message arrived $msg")
                        //test
                        Server.sessions.foreach { n =>
                            n._2 ! "hello"
                        }
                }
                case JsError(e) => logger.warn("unsupported message arrived")
                    Server.sessions.foreach { n =>
                        n._2 ! "sorry"
                    }
            }


        case PeerClosed => context stop self
    }
}
