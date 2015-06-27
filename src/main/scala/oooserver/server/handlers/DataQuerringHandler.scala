package oooserver.server.handlers

import akka.actor.ActorRef
import oooserver.server.api.{GetDataResponse, GetDataRequest}

import scala.concurrent.Future

/**
 * Created by rois on 3/29/15.
 */
object DataQueryingHandler extends BaseHandler[GetDataRequest,GetDataResponse]{
    override def handle(request: GetDataRequest,sender: ActorRef): Future[GetDataResponse] = ???
}
