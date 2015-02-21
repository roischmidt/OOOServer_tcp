package oooserver.server

import java.util.UUID

import authentikat.jwt.{JwtClaimsSet, JwtHeader, JsonWebToken}
import com.fasterxml.jackson.core.JsonParseException

import scala.util.Try

/**
 * Created by rois on 2/21/15.
 */
object TokenManager {

	final lazy val secretKey = UUID.randomUUID().toString

	def createToke(nickname: String) : String = {
		JsonWebToken(JwtHeader("HS256"), JwtClaimsSet(Map("n" -> nickname)), secretKey)
	}

	def validateToken(jwt: String) : Boolean  = {
		try {
			JsonWebToken.validate(jwt, secretKey)
		}
		catch {
			case _: Throwable => false
		}
	}

	def parseTokenClaimNickname(jwt: String) : String  = {
		 jwt match {
			case JsonWebToken(header, claimsSet, signature) =>
				claimsSet.asSimpleMap.get("n")
			case x =>
				""
		}
	}


}
