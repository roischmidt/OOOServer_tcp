package oooserver.test

import oooserver.server.util.TokenManager
import org.scalatest.{FunSpec, Matchers}

class TokenManagerSpec extends FunSpec with Matchers {

    describe("TokenManager tests") {

        it("Token creation") {
            TokenManager.createToken("test").size should be > 0
        }

        it("Token validation") {
            val token = TokenManager.createToken("test")
            TokenManager.validateToken(token) shouldBe true
        }

        it("Token parsing") {
            val token = TokenManager.createToken("test")
            TokenManager.parseTokenClaimUsername(token) shouldBe Some("test")
        }
    }
}
