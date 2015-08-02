package oooserver.server.handlers

import akka.actor.ActorRef
import oooserver.server.api._
import oooserver.server.util.SessionManager
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

object LoginHandler extends BaseHandler[LoginRequest,LoginResponse]{
	override def handle(request: LoginRequest,sender: ActorRef): Future[LoginResponse] =
		SessionManager.addUser(request.nickname,sender).map {
			case true =>
				LoginResponse()
			case false =>
				throw CustomErrorException(s"${request.nickname} already exists",ErrorCode.ERR_USER_ALREADY_EXISTS)
		}.recoverWith{
			case e: Throwable => throw e
		}
}
