package oooserver.server.handlers

import akka.actor.ActorRef
import oooserver.server.api.{GetPlayerListResponse, GetPlayerListRequest}

import scala.concurrent.Future

object GetPlayerListHandler extends BaseHandler[GetPlayerListRequest,GetPlayerListResponse]{
	override def handle(request: GetPlayerListRequest,sender: ActorRef): Future[GetPlayerListResponse] = ???
}
