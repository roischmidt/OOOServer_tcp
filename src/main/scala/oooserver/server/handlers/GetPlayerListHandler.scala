package oooserver.server.handlers

import oooserver.server.api.{GetPlayerListResponse, GetPlayerListRequest}

import scala.concurrent.Future

object GetPlayerListHandler extends BaseHandler[GetPlayerListRequest,GetPlayerListResponse]{
	override def handle(request: GetPlayerListRequest): Future[GetPlayerListResponse] = ???
}
