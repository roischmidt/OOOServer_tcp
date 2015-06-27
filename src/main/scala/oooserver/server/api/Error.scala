package oooserver.server.api

import oooserver.server.util.EnumJson
import play.api.libs.json.Json

object ErrorCode {
	final val ERR_OK = 0
	final val ERR_SYSTEM = 1
	final val ERR_OPPONENT_OCCUPIED = 2
	final val ERR_USER_OFFLINE = 3
	final val ERR_USER_ALREADY_EXISTS = 4
	final val ERR_NO_AVAILABLE_PLAYERS = 5
	final val ERR_NO_OPPONENT = 6
	final val ERR_INVALID_TOKEN = 7
	final val ERR_BAD_REQUEST = 8
}

case class CustomErrorException(msg: String, code: Int) extends scala.Throwable {
	override def toString: String = {
		s"msg:$msg code:${code.toString}"
	}
}

object CustomErrorException {
	implicit val fmtJson = Json.format[CustomErrorException]
}

case class ErrorResponse(
	msg: String,
	code: Int,
	id: Int
) extends Message(id)

object ErrorResponse{
	implicit val fmtJson = Json.format[ErrorResponse]
}

