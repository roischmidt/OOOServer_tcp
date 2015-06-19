import oooserver.server.{SessionManager, UserData}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

class SessionManagerSpec extends FunSpec with Matchers with ScalaFutures with BeforeAndAfter {

    before {
        SessionManager.clearAll()
    }

    describe("SessionManager test") {

        it("store") {
            SessionManager.store("test", UserData("johnDoe", Some("op"), None)).futureValue shouldBe true
        }

        it("get") {
            SessionManager.store("test", UserData("johnDoe", Some("op"), None)).futureValue shouldBe true
            SessionManager.getData("test").futureValue shouldBe Some(UserData("johnDoe", Some("op"), None))
        }

        it("exists") {
            SessionManager.store("test", UserData("johnDoe", Some("op"), None)).futureValue shouldBe true
            SessionManager.exists("test").futureValue shouldBe true
        }

        it("Store with expiration") {
            SessionManager.storeEx("test", 1, UserData("johnDoe", Some("op"), None)).futureValue shouldBe true
            Thread sleep 1000
            SessionManager.getData("test1").futureValue shouldBe None
        }

        it("Resume") {
            SessionManager.storeEx("test", 1, UserData("johnDoe", Some("op"), None)).futureValue shouldBe true
            Thread sleep 200
            SessionManager.resume("test", 1) // should expire in 1 second
            Thread sleep 900
            SessionManager.exists("test").futureValue shouldBe true // should exists because second did not passed yet
            Thread sleep 200
            SessionManager.exists("test").futureValue shouldBe false
        }

        it("get opponent name") {
            SessionManager.store("test", UserData("1234", Some("op"), None)).futureValue shouldBe true
            SessionManager.getOpponentName("test").futureValue shouldBe Some("op")
        }

        it("get sessionId ") {
            SessionManager.store("test", UserData("1234", Some("op"), None)).futureValue shouldBe true
            SessionManager.getSessionId("test").futureValue shouldBe Some("1234")
        }

        it("is palyer paired") {
            SessionManager.store("test", UserData("1234", Some("op"), None)).futureValue shouldBe true
            SessionManager.store("test2", UserData("1234", None, None)).futureValue shouldBe true
            SessionManager.isPaired("test").futureValue shouldBe true
            SessionManager.isPaired("test2").futureValue shouldBe false
        }

        it("online players") {
            SessionManager.store("test", UserData("1234", None, None)).futureValue shouldBe true
            SessionManager.store("test2", UserData("1234", None, None)).futureValue shouldBe true
            SessionManager.store("test3", UserData("1234", None, None)).futureValue shouldBe true
            SessionManager.store("test4", UserData("1234", None, None)).futureValue shouldBe true
            SessionManager.onlinePlayers().futureValue.sorted shouldBe "test" :: "test2" :: "test3" :: "test4" :: Nil

        }

        it("unpaired player list") {
            SessionManager.store("test", UserData("1234", Some("test3"), None)).futureValue shouldBe true
            SessionManager.store("test2", UserData("1234", None, None)).futureValue shouldBe true
            SessionManager.store("test3", UserData("1234", Some("test1"), None)).futureValue shouldBe true
            SessionManager.store("test4", UserData("1234", None, None)).futureValue shouldBe true
            SessionManager.store("test5", UserData("1234", Some("test6"), None)).futureValue shouldBe true
            SessionManager.store("test6", UserData("1234", Some("test5"), None)).futureValue shouldBe true
            SessionManager.freePlayerList().futureValue.sorted shouldBe "test2" :: "test4" :: Nil
        }

        it("find free player") {
            SessionManager.store("test", UserData("1234", Some("test3"), None)).futureValue shouldBe true
            SessionManager.store("test2", UserData("1234", None, None)).futureValue shouldBe true
            SessionManager.store("test3", UserData("1234", Some("test1"), None)).futureValue shouldBe true
            SessionManager.findFreePlayer().futureValue shouldBe Some("test2")
        }

        it("Set opponent") {
            SessionManager.store("test", UserData("1234", None, None)).futureValue shouldBe true
            SessionManager.store("test1", UserData("1234", Some("test1"), None)).futureValue shouldBe true
            SessionManager.setOpponent("test", "test1").futureValue shouldBe true
            SessionManager.getData("test").futureValue shouldBe Some(UserData("1234", Some("test1"), None))
        }

        it("pair 2 players") {
            SessionManager.store("test", UserData("1234", None, None)).futureValue shouldBe true
            SessionManager.store("test1", UserData("1234", None, None)).futureValue shouldBe true
            SessionManager.pairWith("test", "test1").futureValue shouldBe true
            SessionManager.getData("test").futureValue shouldBe Some(UserData("1234", Some("test1"), None))
            SessionManager.getData("test1").futureValue shouldBe Some(UserData("1234", Some("test"), None))
        }

        it("try to pair 2 players when one of them is already paired") {
            SessionManager.store("test", UserData("1234", None, None)).futureValue shouldBe true
            SessionManager.store("test1", UserData("1234", None, None)).futureValue shouldBe true
            SessionManager.store("test2", UserData("1234", None, None)).futureValue shouldBe true
            SessionManager.pairWith("test", "test1").futureValue shouldBe true
            SessionManager.pairWith("test2", "test1").futureValue shouldBe false
        }

        it("unpair players") {
            SessionManager.store("test", UserData("1234", None, None)).futureValue shouldBe true
            SessionManager.store("test1", UserData("1234", None, None)).futureValue shouldBe true
            SessionManager.pairWith("test", "test1").futureValue shouldBe true
            SessionManager.getData("test").futureValue shouldBe Some(UserData("1234", Some("test1"), None))
            SessionManager.getData("test1").futureValue shouldBe Some(UserData("1234", Some("test"), None))
            SessionManager.unpairPlayer("test1").futureValue shouldBe Some("test")
            SessionManager.getData("test1").futureValue shouldBe Some(UserData("1234", None, None))
        }
        it("pair anonymous") {
            SessionManager.store("test", UserData("1234", None, None)).futureValue shouldBe true
            SessionManager.store("test1", UserData("1234", None, None)).futureValue shouldBe true
            SessionManager.pairAnonymous("test1").futureValue shouldBe Some("test")
        }
    }
}
