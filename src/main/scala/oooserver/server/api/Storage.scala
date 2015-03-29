package oooserver.server.api

import play.api.libs.json.Json

/**
 * store data in DB. will be save as long as player is online
 */
case class StoreDataRequest(
	token: String,
	data: Option[Map[String, String]]
)

object StoreDataRequest {
	implicit val fmtJson = Json.format[StoreDataRequest]
}

/**
 * return the number of successful stored fields
 * @param numOfStoredFields
 */
case class StoraDateResponse(
	numOfStoredFields: Int
)

object StoraDateResponse {
	implicit val fmtJson = Json.format[StoraDateResponse]
}


/*
	available ERRORS
	ERR_SYSTEM : in case of unknown exception
 */


/**
 * get stored data from DB
 */
case class GetDataRequest(
	token: String
)

object GetDataRequest {
	implicit val fmtJson = Json.format[GetDataRequest]
}

/**
 * return the stored data. None if empty
 * @param data
 */
case class GetDataResponse(
	data: Option[Map[String, String]]
)

object GetDataResponse {
	implicit val fmtJson = Json.format[GetDataResponse]
}


/*
	available ERRORS
	ERR_SYSTEM : in case of unknown exception
 */

