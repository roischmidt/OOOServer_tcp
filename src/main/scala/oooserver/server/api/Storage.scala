package oooserver.server.api

import play.api.libs.json.Json

/**
 * store data in DB. will be save as long as player is online
 */
case class StoreDataRequest(
    token: String,
    data: Option[Map[String, String]],
    id: Int
) extends Message(id)

object StoreDataRequest {
    implicit val fmtJson = Json.format[StoreDataRequest]
}

/**
 * return the number of successful stored fields
 * @param numOfStoredFields
 */
case class StoreDateResponse(
    numOfStoredFields: Int,
    id: Int = MessageId.STORE_DATA_RESPONSE
) extends Message(id)

object StoreDateResponse {
    implicit val fmtJson = Json.format[StoreDateResponse]
}

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
    data: Option[Map[String, String]],
    id: Int = MessageId.GET_DATA_RESPONSE
) extends Message(id)

object GetDataResponse {
    implicit val fmtJson = Json.format[GetDataResponse]
}
