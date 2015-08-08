package oooserver.server.api

import play.api.libs.json._

/**
 * Created by rois on 5/27/15.
 */
class Message(id: Int)

object Message {

    implicit val fmtJsonWrites = new Writes[Message] {
        def writes(msg: Message) = msg match {
            case m : LoginRequest => LoginRequest.fmtJson.writes(m)
            case m : LogoutRequest => LogoutRequest.fmtJson.writes(m)

        }
    }

    implicit val fmtJsonReads = new Reads[Message] {
        def reads(json: JsValue) : JsResult[Message] = (json \ "id").as[Int] match {
            case MessageId.LOGIN_REQUEST =>
                println("trying to parse login request")
                LoginRequest.fmtJson.reads(json)
            case MessageId.LOGOUT_REQUEST =>
                println("trying to parse logout request")
                LogoutRequest.fmtJson.reads(json)
            case _ => JsError("trying to parse unknown message")
        }
    }
}
