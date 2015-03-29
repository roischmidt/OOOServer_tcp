package oooserver.server.handlers

import oooserver.server.api.{ChatResponse, ChatRequest}

import scala.concurrent.Future

object ChatHandler extends BaseHandler[ChatRequest,ChatResponse] {

    def handle(chatRequest: ChatRequest) : Future[ChatResponse] = ???
}
