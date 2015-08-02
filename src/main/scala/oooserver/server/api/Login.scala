package oooserver.server.api

import play.api.libs.json.Json

case class LoginRequest(
    nickname: String,
    id: Int
) extends Message(id)

object LoginRequest {
    implicit val fmtJson = Json.format[LoginRequest]
}

case class LoginResponse(
    id: Int = MessageId.LOGIN_RESPONSE
) extends Message(id)

object LoginResponse {
    implicit val fmtJson = Json.format[LoginResponse]
}

/*
	available ERRORS
	ERR_SYSTEM : in case of unknown exception
	ERR_USER_ALREADY_EXISTS : in case of the nickname already taken

 */



