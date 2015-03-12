import oooserver.server.{CacheData, SessionManager}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

class SessionManagerSpec extends FunSpec with Matchers with ScalaFutures with BeforeAndAfter{

  before{
    SessionManager.clearAll()
  }

  describe("SessionManager test") {

    it("store") {
      SessionManager.store("test", CacheData("johnDoe", Some("op"),None)).futureValue shouldBe true
    }

    it("get") {
      SessionManager.store("test", CacheData("johnDoe", Some("op"),None)).futureValue shouldBe true
      SessionManager.get("test").futureValue shouldBe Some(CacheData("johnDoe", Some("op"),None))
    }

    it("exists"){
      SessionManager.store("test", CacheData("johnDoe", Some("op"),None)).futureValue shouldBe true
      SessionManager.exists("test").futureValue shouldBe true
    }

    it("Store with expiration") {
      SessionManager.storeEx("test", 1, CacheData("johnDoe", Some("op"),None)).futureValue shouldBe true
      Thread sleep 1000
      SessionManager.get("test1").futureValue shouldBe None
    }

    it("Resume"){
      SessionManager.storeEx("test", 1, CacheData("johnDoe", Some("op"),None)).futureValue shouldBe true
      Thread sleep 200
      SessionManager.resume("test",1) // should expire in 1 second
      Thread sleep 900
      SessionManager.exists("test").futureValue shouldBe true // should exists because second did not passed yet
      Thread sleep 200
      SessionManager.exists("test").futureValue shouldBe false
    }
  }
}
