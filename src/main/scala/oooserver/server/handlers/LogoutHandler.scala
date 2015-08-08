package oooserver.server.handlers

import akka.actor.ActorRef
import oooserver.server.api.{LogoutResponse, LogoutRequest}
import oooserver.server.util.SessionManager
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

object LogoutHandler extends BaseHandler[LogoutRequest,LogoutResponse]{
	override def handle(request: LogoutRequest,sender: ActorRef): Future[LogoutResponse] =
	// if removeUserBySessionRef returns with false, means that user not in DB anymore so he is technically logedout anyway
		SessionManager.removeUserBySessionRef(sender).map { _ =>
			LogoutResponse()
		}
}
