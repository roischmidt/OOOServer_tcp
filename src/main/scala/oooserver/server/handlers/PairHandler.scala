package oooserver.server.handlers

import akka.actor.ActorRef
import oooserver.server.api.{PairResponse, PairRequest}

import scala.concurrent.Future

object PairHandler extends BaseHandler[PairRequest,PairResponse]{
	override def handle(request: PairRequest,sender: ActorRef): Future[PairResponse] = ???
}
