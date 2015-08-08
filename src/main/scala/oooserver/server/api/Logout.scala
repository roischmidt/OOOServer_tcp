package oooserver.server.api

import play.api.libs.json.Json

case class LogoutRequest (
	id: Int = MessageId.LOGOUT_REQUEST
) extends Message(id)


object LogoutRequest{
	implicit val fmtJson = Json.format[LogoutRequest]
}

case class LogoutResponse(
	id: Int = MessageId.LOGOUT_RESPONSE
) extends Message(id)


object LogoutResponse {
	implicit val fmtJson = Json.format[LogoutResponse]
}


/*
	available ERRORS
	ERR_SYSTEM : in case of unknown exception
 */

