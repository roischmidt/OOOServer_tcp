package oooserver.server.handlers

import akka.actor.ActorRef
import oooserver.server.api.{LogoutResponse, LogoutRequest}

import scala.concurrent.Future

object LogoutHandler extends BaseHandler[LogoutRequest,LogoutResponse]{
	override def handle(request: LogoutRequest,sender: ActorRef): Future[LogoutResponse] = ???
}
