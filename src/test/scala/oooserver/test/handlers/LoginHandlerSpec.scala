package oooserver.test.handlers

import oooserver.server.api.{CustomErrorException, ErrorCode, LoginRequest, MessageId}
import oooserver.server.handlers.LoginHandler
import oooserver.server.util.SessionManager
import oooserver.test.mock.TestClient
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * Created by rois on 7/15/15.
 */
class LoginHandlerSpec extends FunSpec with Matchers with ScalaFutures with BeforeAndAfter {

    val nickname = "testUser"

    val duration = Duration("10 seconds")

    implicit override val patienceConfig =
        PatienceConfig(timeout = Span(10, Seconds), interval = Span(5, Millis))

    before {
        Await.result(SessionManager.clearAll(),duration)
    }

    describe("login handling") {
        it("login") {
            // first, check if user already exists in storage - shouldn't
            SessionManager.exists(nickname).futureValue shouldBe false
            SessionManager.onlinePlayers().size shouldBe 0

            val loginRequest = LoginRequest(nickname, MessageId.LOGIN_REQUEST)
            whenReady(LoginHandler.handle(loginRequest, TestClient.zserver)) { loginRespons =>
                loginRespons.id shouldBe MessageId.LOGIN_RESPONSE
                SessionManager.exists(nickname).futureValue shouldBe true
                SessionManager.onlinePlayers().size shouldBe 1
            }
        }

        it("login with same user twice") {
            val loginRequest = LoginRequest(nickname, MessageId.LOGIN_REQUEST)
            whenReady(LoginHandler.handle(loginRequest, TestClient.zserver)) { loginRespons =>
                SessionManager.exists(nickname).futureValue shouldBe true
                SessionManager.onlinePlayers().size shouldBe 1
            }

            val thrown = intercept[CustomErrorException] {
                val err = Await.result(LoginHandler.handle(loginRequest, TestClient.zserver), duration)
            }
            assert(thrown.code === ErrorCode.ERR_USER_ALREADY_EXISTS)

        }
    }
    
}
