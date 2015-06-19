package oooserver.server.handlers

import oooserver.server.{Server, Controller, TokenManager, SessionManager}
import oooserver.server.api._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by rois on 4/1/15.
 */
object LeaveOpponentHandler extends BaseHandler[LeaveOpponentRequest,LeaveOpponentResponse]{

	override def handle(request: LeaveOpponentRequest): Future[LeaveOpponentResponse] =
		TokenManager.parseTokenClaimUsername(request.token) match {
			case Some(username) =>
				SessionManager.unpairPlayer(username).flatMap{
					case Some(opName) =>
						SessionManager.getData(opName).map {
							case Some(cd) =>
								Server.send(cd.sessionId,LeaveNotification(username,"User left"))
								LeaveOpponentResponse(opName)
						}
					case None =>
						throw CustomErrorException("No opponent",ErrorCode.ERR_NO_OPPONENT)
				}
			case None =>
				throw CustomErrorException("Can't get username from token",ErrorCode.ERR_INVALID_TOKEN)
		}
}
