import oooserver.server.TokenManager
import org.scalatest.{Matchers, FunSpec}

class TokenManagerSpec extends FunSpec with Matchers{

  describe("TokenManager tests"){

    it("Token creation"){
      TokenManager.createToke("test").size should be > 0
    }

    it("Token validation"){
      val token = TokenManager.createToke("test")
      TokenManager.validateToken(token) shouldBe true
    }

    it("Token parsing"){
      val token = TokenManager.createToke("test")
      TokenManager.parseTokenClaimUsername(token) shouldBe Some("test")
    }
  }
}
