import oooserver.server.{CacheData, SessionManager}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FunSpec, Matchers}

class SessionManagerSpec extends FunSpec with Matchers with ScalaFutures {

  describe("SessionManager test") {

    it("Store") {
      SessionManager.store("test", CacheData("johnDoe", "op")).futureValue shouldBe true
    }

    it("Get") {
      SessionManager.get("test").futureValue shouldBe Some(CacheData("johnDoe", "op"))
    }

    it("Store with expiration") {
      SessionManager.storeEx("test1", 1, CacheData("johnDoe", "op")).futureValue shouldBe true
      Thread sleep 1000
      SessionManager.get("test1").futureValue shouldBe None
    }
  }
}
