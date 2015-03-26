package oooserver.server.api

import play.api.libs.json.Json

/**
 * asking to pair a free opponent
 * if no opponent nick name provided, pair anonymous (to a random free player)
 */
case class PairRequest (
	token: String,
	opponentNickname: Option[String]
)

object PairRequest {
	implicit val fmtJson = Json.format[PairRequest]
}

/*
	available ERRORS
	ERR_SYSTEM : in case of unknown exception
	ERR_OPPONENT_OCCUPIED : in case that requested opponent already occupied
	ERR_NO_AVAILABLE_PLAYERS : no free player to pair with
 */

/**
	if opponent agrees, response with his nickaname
 */
case class PairResponse(
	opponentNickname: String
)

object PairResponse {
	implicit val fmtJson = Json.format[PairResponse]
}

/**
	receive a pair request from opponent $nickname
  */
case class PairNotification(
	nickname: String
)

/**
	approve the pair request from opponent $nicknameAPI
  */
case class PairApprovalRequest(
	token: String,
	nickname: String
)

/*
	available ERRORS
	ERR_SYSTEM : in case of unknown exception
	ERR_OPPONENT_OCCUPIED : opponent have been paired to another user before I had the chance to approve
 */




