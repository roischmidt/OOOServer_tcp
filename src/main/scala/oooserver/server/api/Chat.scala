package oooserver.server.api

import play.api.libs.json.Json

case class ChatRequest(
	token: String,
	msg: String
)

object ChatRequest {
	implicit val fmtJson = Json.format[ChatRequest]
}

case class ChatNotification(
	msg: String
)

object ChatNotification {
	implicit val fmtJson = Json.format[ChatNotification]
}

/*
	available ERRORS
	ERR_SYSTEM : in case of unknown exception
	ERR_USER_OFFLINE: Opponent is not available anymore
	ERR_NO_OPPONENT: Your opponent is no longer paired with you so he can't receive your message
 */

