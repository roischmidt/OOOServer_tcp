package oooserver.server.api

import play.api.libs.json.Json

/**
 * asking to pair a free opponent
 * if no opponent nick name provided, pair anonymous (to a random free player)
 */
case class PairRequest (
	token: String,
	opponentNickname: Option[String],
	id: Int
) extends Message(id)

object PairRequest {
	implicit val fmtJson = Json.format[PairRequest]
}

/**
	if opponent agrees, response with his nickaname
 */
case class PairResponse(
	opponentNickname: String,
	id: Int = MessageId.PAIR_RESPONSE
) extends Message(id)

object PairResponse {
	implicit val fmtJson = Json.format[PairResponse]
}

/**
	receive a pair request from opponent $nickname
  */
case class PairNotification(
	nickname: String,
	id: Int = MessageId.PAIR_NOTIFICATION
) extends Message(id)

/**
	approve the pair request from opponent $nicknameAPI
  */
case class PairApprovalRequest(
	token: String,
	nickname: String,
	id: Int
) extends Message(id)





