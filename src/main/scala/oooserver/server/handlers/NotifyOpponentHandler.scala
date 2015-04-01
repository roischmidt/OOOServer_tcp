package oooserver.server.handlers

import oooserver.server.api.{NotifyOpponentResponse, NotifyOpponentRequest}

import scala.concurrent.Future

object NotifyOpponentHandler extends BaseHandler[NotifyOpponentRequest,NotifyOpponentResponse]{
	override def handle(request: NotifyOpponentRequest): Future[NotifyOpponentResponse] = ???
}
