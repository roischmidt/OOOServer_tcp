package oooserver.server.api

import play.api.libs.json.Json

case class LoginRequest(
	nickname: String
)

object LoginRequest{
	implicit val fmtJson = Json.format[LoginRequest]
}

case class LoginResponse(
	token: String
)

object LoginResponse{
	implicit val fmtJson = Json.format[LoginResponse]
}

/*
	available ERRORS
	ERR_SYSTEM : in case of unknown exception
	ERR_USER_ALREADY_EXISTS : in case of the nickname already taken

 */



