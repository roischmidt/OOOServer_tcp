package oooserver.server.handlers

import akka.actor.ActorRef
import oooserver.server.api.{NotifyOpponentResponse, NotifyOpponentRequest}

import scala.concurrent.Future

object NotifyOpponentHandler extends BaseHandler[NotifyOpponentRequest,NotifyOpponentResponse]{
	override def handle(request: NotifyOpponentRequest,sender: ActorRef): Future[NotifyOpponentResponse] = ???
}
