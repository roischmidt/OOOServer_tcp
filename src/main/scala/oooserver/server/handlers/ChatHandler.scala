package oooserver.server.handlers

import oooserver.server.api.{ChatResponse, ChatRequest}
import oooserver.server.util.TokenManager

import scala.concurrent.Future

object ChatHandler extends BaseHandler[ChatRequest,ChatResponse] {

    def handle(chatRequest: ChatRequest) : Future[ChatResponse] = ???
    /* {
        // extract username from token
        TokenManager.parseTokenClaimUsername(chatRequest.token).map{username =>
            SessionManager.get(username).map{
                case Some(cd) =>
                    cd.opponent.isDefined match {
                        case true => /*SessionManager.get(cd.opponent.get).map{cdOpt =>
                            cdOpt.map{cd =>
                                cd.sessionId
                            }*/
                            ???

                        case false => ???
                    }
            }
        }
    }*/
}
