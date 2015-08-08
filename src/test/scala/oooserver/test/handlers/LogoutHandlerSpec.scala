package oooserver.test.handlers

import oooserver.server.api._
import oooserver.server.handlers.{LogoutHandler, LoginHandler}
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
class LogoutHandlerSpec extends FunSpec with Matchers with ScalaFutures with BeforeAndAfter {

    val nickname = "testUser"

    val duration = Duration("10 seconds")

    implicit override val patienceConfig =
        PatienceConfig(timeout = Span(10, Seconds), interval = Span(5, Millis))

    before {
        Await.result(SessionManager.clearAll(),duration)
    }

    describe("login handling") {
        it("logout") {
            // first, check if user already exists in storage - shouldn't
            SessionManager.exists(nickname).futureValue shouldBe false
            SessionManager.onlinePlayers().size shouldBe 0

            val loginRequest = LoginRequest(nickname, MessageId.LOGIN_REQUEST)
            whenReady(LoginHandler.handle(loginRequest, TestClient.zserver)) { loginRespons =>
                SessionManager.onlinePlayers().size shouldBe 1
            }

            val logoutRequest = LogoutRequest()
            whenReady(LogoutHandler.handle(logoutRequest, TestClient.zserver)) { logoutResponse =>
                SessionManager.onlinePlayers().size shouldBe 0
            }
        }

        it("logout twice") {
            val loginRequest = LoginRequest(nickname, MessageId.LOGIN_REQUEST)

            whenReady(LoginHandler.handle(loginRequest, TestClient.zserver)) { loginRespons =>
                SessionManager.onlinePlayers().size shouldBe 1
            }

            val logoutRequest = LogoutRequest()
            whenReady(LogoutHandler.handle(logoutRequest, TestClient.zserver)) { logoutResponse =>
                SessionManager.onlinePlayers().size shouldBe 0
            }

            whenReady(LogoutHandler.handle(logoutRequest, TestClient.zserver)) { logoutResponse =>
               logoutResponse.id shouldBe MessageId.LOGOUT_RESPONSE
                SessionManager.onlinePlayers().size shouldBe 0
            }

        }
    }
    
}
