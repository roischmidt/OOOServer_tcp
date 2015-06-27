package oooserver.server.handlers

import akka.actor.ActorRef
import akka.io.Tcp
import oooserver.server.SessionManager
import oooserver.server.api._
import org.slf4j.LoggerFactory
import play.api.libs.json.{JsResult, JsSuccess, JsValue, Json}

import scala.concurrent.Future
import scala.util.Try

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by rois on 6/20/15.
 */
//object Handler {
//
//    final val logger = LoggerFactory.getLogger(this.getClass)
//
//    def receive(msg: String, sender: ActorRef) = {
//        logger.info(s"message to be handled $msg")
//
//        try {
//            val json = Json.parse(msg)
//
//            (json \ "id").asOpt[Int] match {
//                case Some(MessageId.LOGIN_REQUEST) =>
//                    logger.info("login request arrived")
//                   LoginHandler.handle(LoginRequest.fmtJson.reads(json).get,sender).map{response =>
//                     logger.info(s"$response to send back")
//                    sender ! LoginResponse.fmtJson.writes(response).toString()
//                    } recoverWith {
//                       case e : CustomErrorException => sender ! e.toString
//                           logger.error(e.toString)
//                           Future.successful("")
//                   }
//                case _ =>
//                    Future.successful("{}")
//            }
//
//        } catch {
//            case _: Throwable =>
//                Future.successful(ErrorResponse.fmtJson.writes(ErrorResponse("Bad request",ErrorCode.ERR_BAD_REQUEST)).toString())
//        }
//    }
//}