package oooserver.server.handlers

import oooserver.server.api.{LeaveOpponentResponse, LeaveOpponentRequest}

import scala.concurrent.Future

/**
 * Created by rois on 4/1/15.
 */
object LeaveOpponentHandler extends BaseHandler[LeaveOpponentRequest,LeaveOpponentResponse]{
	override def handle(request: LeaveOpponentRequest): Future[LeaveOpponentResponse] = ???
}
