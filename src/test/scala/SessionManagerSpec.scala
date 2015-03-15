import oooserver.server.api.{ErrorCode, CustomErrorException}
import oooserver.server.{CacheData, SessionManager}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

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
    /*


  def pairWith(op1: String, op2: String): Future[Boolean] =
    isPaired(op1).flatMap{
      case true => throw new Throwable(s"$op1 is already paired")
      case false =>
      setOpponent(op1, op2).flatMap {
        case true => setOpponent(op2, op1)
        case false => Future.successful(false)
      }
    }

  def pairAnonymous(username: String): Future[String] =
    isPaired(username).flatMap{
      case true => throw new Throwable(s"$username is already paired")
      case false =>
      findFreePlayer.flatMap { fp =>
        pairWith(username, fp).map {
          case true => fp
          case false => throw new Throwable("Problem with pair")
        }
      }
    }

  def getFromMemory(username: String,key: String): Future[Option[String]] =
    get(username).map { cdOp =>
        cdOp.flatMap{cd =>
          cd.memory.flatMap{mem =>
            mem.get(key)
          }
        }
      }
     */
    it("is palyer paired"){
      SessionManager.store("test", CacheData("1234", Some("op"),None)).futureValue shouldBe true
      SessionManager.store("test2", CacheData("1234", None,None)).futureValue shouldBe true
      SessionManager.isPaired("test").futureValue shouldBe true
      SessionManager.isPaired("test2").futureValue shouldBe false
    }

    it("online players"){
      SessionManager.store("test", CacheData("1234", None,None)).futureValue shouldBe true
      SessionManager.store("test2", CacheData("1234", None,None)).futureValue shouldBe true
      SessionManager.store("test3", CacheData("1234", None,None)).futureValue shouldBe true
      SessionManager.store("test4", CacheData("1234", None,None)).futureValue shouldBe true
      SessionManager.onlinePlayers().futureValue.sorted shouldBe "test"::"test2"::"test3"::"test4"::Nil

    }

    it("unpaired player list"){
      SessionManager.store("test", CacheData("1234", Some("test3"),None)).futureValue shouldBe true
      SessionManager.store("test2", CacheData("1234", None,None)).futureValue shouldBe true
      SessionManager.store("test3", CacheData("1234", Some("test1"),None)).futureValue shouldBe true
      SessionManager.store("test4", CacheData("1234", None,None)).futureValue shouldBe true
      SessionManager.store("test5", CacheData("1234", Some("test6"),None)).futureValue shouldBe true
      SessionManager.store("test6", CacheData("1234", Some("test5"),None)).futureValue shouldBe true
      SessionManager.unPairedPlayers().futureValue.sorted shouldBe "test2"::"test4"::Nil
    }

    it("find free player") {
      SessionManager.store("test", CacheData("1234", Some("test3"), None)).futureValue shouldBe true
      SessionManager.store("test2", CacheData("1234", None, None)).futureValue shouldBe true
      SessionManager.store("test3", CacheData("1234", Some("test1"), None)).futureValue shouldBe true
      SessionManager.findFreePlayer().futureValue shouldBe "test2"
    }

    it("Set opponent"){
      SessionManager.store("test", CacheData("1234", None, None)).futureValue shouldBe true
      SessionManager.store("test1", CacheData("1234", Some("test1"), None)).futureValue shouldBe true
      SessionManager.setOpponent("test","test1").futureValue shouldBe true
      SessionManager.get("test").futureValue shouldBe Some(CacheData("1234", Some("test1"),None))
    }

    it("pair 2 players"){
      SessionManager.store("test", CacheData("1234", None, None)).futureValue shouldBe true
      SessionManager.store("test1", CacheData("1234", None, None)).futureValue shouldBe true
      SessionManager.pairWith("test","test1").futureValue shouldBe true
      SessionManager.get("test").futureValue shouldBe Some(CacheData("1234", Some("test1"),None))
      SessionManager.get("test1").futureValue shouldBe Some(CacheData("1234", Some("test"),None))
    }

    it("try to pair 2 players when one of them is already paired"){
      SessionManager.store("test", CacheData("1234", None, None)).futureValue shouldBe true
      SessionManager.store("test1", CacheData("1234", None, None)).futureValue shouldBe true
      SessionManager.store("test2", CacheData("1234", None, None)).futureValue shouldBe true
      SessionManager.pairWith("test","test1").futureValue shouldBe true
      val thrown = intercept[CustomErrorException] {
        Await.result(SessionManager.pairWith("test2","test1"), Duration("5 second"))
      }
      assert(thrown.code === ErrorCode.ERR_USER_ALREADY_PAIRED)
    }
  }
}
