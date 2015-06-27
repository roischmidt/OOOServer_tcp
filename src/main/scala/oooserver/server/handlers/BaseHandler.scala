package oooserver.server.handlers

import akka.actor.ActorRef

import scala.concurrent.Future


trait BaseHandler[REQ,RES] {

    def handle(request: REQ,sender: ActorRef) : Future[RES]
}
