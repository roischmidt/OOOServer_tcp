package oooserver.server.api

import oooserver.server.util.EnumJson
import play.api.libs.json.Json

object ErrorCode {
  final val ERR_SYSTEM = 0
  final val ERR_USER_ALREADY_PAIRED = 1
  final val ERR_USER_OFFLINE = 2

}

case class CustomErrorException(msg: String, code: Int) extends scala.Throwable {
  override def toString : String = {
    s"msg:$msg code:${code.toString}"
  }
}

object CustomErrorException {
  implicit val fmtJson = Json.format[CustomErrorException]
}

