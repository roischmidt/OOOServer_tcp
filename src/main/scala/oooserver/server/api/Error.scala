package oooserver.server.api

import play.api.libs.json.Json

object ErrorCode {
	final val ERR_SYSTEM = 0
	final val ERR_USER_ALREADY_PAIRED = 1
	final val ERR_USER_OFFLINE = 2
	final val ERR_USER_ALREADY_EXISTS = 3
	final val ERR_NO_AVAILABLE_PLAYERS = 4

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
	code: Int
)

object ErrorResponse{
	implicit val fmtJson = Json.format[ErrorResponse]
}

