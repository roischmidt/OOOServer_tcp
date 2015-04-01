package oooserver.server.handlers

import oooserver.server.api.{PairResponse, PairRequest}

import scala.concurrent.Future

object PairHandler extends BaseHandler[PairRequest,PairResponse]{
	override def handle(request: PairRequest): Future[PairResponse] = ???
}
