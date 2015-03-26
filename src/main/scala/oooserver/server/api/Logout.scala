package oooserver.server.api

import play.api.libs.json.Json

case class LogoutRequest (
	token: String
)



object LogoutRequest{
	implicit val fmtJson = Json.format[LogoutRequest]
}


/*
	available ERRORS
	ERR_SYSTEM : in case of unknown exception
 */

