package oooserver.server.handlers

import akka.actor.ActorRef
import oooserver.server.api.{StoreDateResponse, StoreDateResponse$, StoreDataRequest}

import scala.concurrent.Future

object DataStoringHandler extends BaseHandler[StoreDataRequest,StoreDateResponse]{
    override def handle(request: StoreDataRequest,sender: ActorRef): Future[StoreDateResponse] = ???
}
