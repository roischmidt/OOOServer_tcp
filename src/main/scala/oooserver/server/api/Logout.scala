package oooserver.server.api

import play.api.libs.json.Json

case class LogoutRequest (
	token: String,
	id: Int
) extends Message(id)


object LogoutRequest{
	implicit val fmtJson = Json.format[LogoutRequest]
}

case class LogoutResponse(
	reason: Int ,// can be ERR_USER_OFFLINE : user disconnected. ERR_NO_OPPONENT : user decided to leave
	id: Int
) extends Message(id)


object LogoutResponse {
	implicit val fmtJson = Json.format[LogoutResponse]
}


/*
	available ERRORS
	ERR_SYSTEM : in case of unknown exception
 */

