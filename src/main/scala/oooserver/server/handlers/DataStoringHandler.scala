package oooserver.server.handlers

import oooserver.server.api.{StoraDateResponse, StoreDataRequest}

import scala.concurrent.Future

object DataStoringHandler extends BaseHandler[StoreDataRequest,StoraDateResponse]{
    override def handle(request: StoreDataRequest): Future[StoraDateResponse] = ???
}
