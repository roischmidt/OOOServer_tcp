package oooserver.server.util

import java.util.UUID

import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}

/**
 * Created by rois on 2/21/15.
 */
object TokenManager {

	final lazy val secretKey = UUID.randomUUID().toString

	def createToken(username: String) : String = {
		JsonWebToken(JwtHeader("HS256"), JwtClaimsSet(Map("u" -> username)), secretKey)
	}

	def validateToken(jwt: String) : Boolean  = {
		try {
			JsonWebToken.validate(jwt, secretKey)
		}
		catch {
			case _: Throwable => false
		}
	}

	def parseTokenClaimUsername(jwt: String) : Option[String]  = {
		 jwt match {
			case JsonWebToken(header, claimsSet, signature) =>
				Some(claimsSet.asSimpleMap.get("u"))
			case x =>
				None
		}
	}


}
