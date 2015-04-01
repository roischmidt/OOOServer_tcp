package oooserver.server.handlers

import oooserver.server.api.{LoginResponse, LoginRequest}

import scala.concurrent.Future

object LoginHandler extends BaseHandler[LoginRequest,LoginResponse]{
	override def handle(request: LoginRequest): Future[LoginResponse] = ???
}
