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
				LoginResponse("token")
			case false =>
				throw CustomErrorException(s"${request.nickname} can't login right now. already online or an error occurred",ErrorCode.ERR_SYSTEM)
		}
}
