package oooserver.server.handlers

import scala.concurrent.Future


trait BaseHandler[REQ,RES] {

    def handle(request: REQ) : Future[RES]
}
