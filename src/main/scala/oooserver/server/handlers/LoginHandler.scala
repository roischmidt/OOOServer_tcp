package oooserver.server.handlers

import akka.actor.ActorRef
import oooserver.server.SessionManager
import oooserver.server.api._

import scala.concurrent.Future

object LoginHandler extends BaseHandler[LoginRequest,LoginResponse]{
	override def handle(request: LoginRequest,sender: ActorRef): Future[LoginResponse] =
	//	SessionManager.get(request.nickname).isDefined match {
		//	case true =>
		//		throw new CustomErrorException("Nickname already taken",ErrorCode.ERR_USER_ALREADY_EXISTS)
	//		case false =>
				//SessionManager.add(request.nickname,sender)
				Future.successful(LoginResponse("token"))
	//	}

	/*{
		SessionManager.add(request.nickname,)
		Future.successful(LoginResponse("1234"))
	}*/
}
