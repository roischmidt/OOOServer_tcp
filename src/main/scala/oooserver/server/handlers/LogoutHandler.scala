package oooserver.server.handlers

import oooserver.server.api.{LogoutResponse, LogoutRequest}

import scala.concurrent.Future

object LogoutHandler extends BaseHandler[LogoutRequest,LogoutResponse]{
	override def handle(request: LogoutRequest): Future[LogoutResponse] = ???
}
