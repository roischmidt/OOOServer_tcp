package oooserver.test.handlers

import oooserver.server.api
import oooserver.server.api.{MessageId, LoginRequest}
import oooserver.server.handlers.LoginHandler
import oooserver.server.util.SessionManager
import oooserver.test.mock.TestClient
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfter, Matchers, FunSpec}

/**
 * Created by rois on 7/15/15.
 */
class LoginHandlerSpec extends FunSpec with Matchers with ScalaFutures with BeforeAndAfter {

    val nickname = "testUser"

    before {
        SessionManager.clearAll()
    }

    describe("login handling") {
        it("login") {
            // first, check if user already exists in storage - shouldn't
            SessionManager.exists(nickname).futureValue shouldBe false
            SessionManager.onlinePlayers().size shouldBe 0

            val loginRequest = LoginRequest(nickname,MessageId.LOGIN_REQUEST)
            whenReady(LoginHandler.handle(loginRequest,TestClient.zserver)) { loginRespons =>
                loginRespons.id shouldBe MessageId.LOGIN_RESPONSE
                SessionManager.exists(nickname).futureValue shouldBe true
                SessionManager.onlinePlayers().size shouldBe 1
            }
        }
    }
    
}
