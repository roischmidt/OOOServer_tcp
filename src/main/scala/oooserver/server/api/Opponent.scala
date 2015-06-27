package oooserver.server.api

import play.api.libs.json.Json

/**
 * Send Opponent data
 */
case class NotifyOpponentRequest (
	token: String,
	data: Map[String, String],
	id: Int
) extends Message(id)

object NotifyOpponentRequest {
	implicit val fmtJson = Json.format[NotifyOpponentRequest]
}

case class NotifyOpponentResponse(
	reason: Int, // can be ERR_USER_OFFLINE : user disconnected. ERR_NO_OPPONENT : user decided to leave
	id: Int = MessageId.NOTIFY_OPPONENT_RESPONSE
) extends Message(id)


object NotifyOpponentResponse {
	implicit val fmtJson = Json.format[NotifyOpponentResponse]
}


/*
	available ERRORS
	ERR_SYSTEM : in case of unknown exception
	ERR_USER_OFFLINE: Opponent is not available anymore
	ERR_NO_OPPONENT: Your opponent is no longer paired with you so he can't receive your message
 */

/**
* Got notification from opponent
*/
case class OpponentNotification(
	data: Map[String, String],
	id: Int = MessageId.NOTIFY_OPPONENT_NOTIFICATION
) extends Message(id)

object OpponentNotification {
	implicit val fmtJson = Json.format[OpponentNotification]
}
