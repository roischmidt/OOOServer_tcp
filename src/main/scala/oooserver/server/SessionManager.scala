package oooserver.server

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.util.Timeout
import com.redis.RedisClient
import play.api.libs.json.{JsSuccess, Json}
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.language.postfixOps


case class CacheData(nickname: String, Opponent: String)
object CacheData { implicit val fmtJson = Json.format[CacheData]}

object SessionManager {

	final val expired = TimeUnit.SECONDS.convert(5, TimeUnit.MINUTES).toInt // session expires after 5 minutes of inactivity

	// Akka setup
	implicit val system = ActorSystem("redis-client")
	implicit val executionContext = system.dispatcher
	implicit val timeout = Timeout(5 seconds)

	// Redis client setup
	val client = RedisClient("localhost", 6379)

	def clearAll() = client.flushdb()

	def store(key: String,value: CacheData): Future[Boolean] =
		client.setex(key,expired,Json.toJson(value).toString())

	def resume(key: String) : Future[Boolean] =
		client.expire(key,expired)

	def exists(key: String) : Future[Boolean] =
		client.exists(key)

	def get(key: String) : Future[CacheData] = {
		client.get(key).map{x=>
			CacheData.fmtJson.reads(Json.parse(x.get.asInstanceOf[String])) match {
								case JsSuccess(d,_) => d
								case _ => throw new IllegalStateException("Couldn't parse cached data")
							}
		}
	}

}
