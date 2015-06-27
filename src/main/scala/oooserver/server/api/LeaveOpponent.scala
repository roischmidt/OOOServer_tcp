package oooserver.server.api

import play.api.libs.json.Json


/**
 * leave session with your opponent
 */
case class LeaveOpponentRequest(
    token: String,
    id: Int
) extends Message(id)

object LeaveOpponentRequest {
    implicit val fmtJson = Json.format[LeaveOpponentRequest]
}

case class LeaveOpponentResponse(
    opponentName: String,
    id: Int = MessageId.LEAVE_OPPONENT_RESPONSE
) extends Message(id)

object LeaveOpponentResponse {
    implicit val fmtJson = Json.format[LeaveOpponentResponse]
}

/*
	available ERRORS
	ERR_SYSTEM : in case of unknown exception
	ERR_NO_OPPONENT: you are not paired to any opponent
 */

/**
 * revive a leave request from your opponent. after this you are no longer paired
 */
case class LeaveNotification(
    oppnentName: String,
    reason: String, // can be ERR_USER_OFFLINE : user disconnected. ERR_NO_OPPONENT : user decided to leave
    id: Int = MessageId.LEAVE_NOTIFICATION
) extends Message(id)

object LeaveNotification {
    implicit val fmtJson = Json.format[LeaveNotification]
}
