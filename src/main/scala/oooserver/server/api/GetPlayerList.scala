package oooserver.server.api

import play.api.libs.json.Json

case class PlayerData(
	nickname: String,
	opponent: Option[String]
)

object PlayerData{
	implicit val fmtJson = Json.format[PlayerData]
}

// gets the whole online player list
case class GetPlayerListRequest(
	token: String
)

object GetPlayerListRequest{
	implicit val fmtJson = Json.format[GetPlayerListRequest]
}

case class GetPlayerListResponse(
	players: List[PlayerData]
)

object GetPlayerListResponse{
	implicit val fmtJson = Json.format[GetPlayerListResponse]
}

/*
	available ERRORS
	ERR_SYSTEM : in case of unknown exception

 */

