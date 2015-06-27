package oooserver.server

import java.net.InetSocketAddress

import akka.actor._
import akka.io.{IO, Tcp}
import com.typesafe.config.ConfigFactory
import oooserver.server.api.{LogoutRequest, LoginRequest, Message}
import oooserver.server.util.SessionManager
import org.slf4j.LoggerFactory
import play.api.libs.json.{JsError, JsSuccess, Json}

/**
 * Created by rois on 2/19/15.
 */

class Server extends Actor {

    final val logger = LoggerFactory.getLogger(this.getClass)

    import Tcp._
    import context.system

    IO(Tcp) ! Bind(self, new InetSocketAddress(ConfigFactory.load.getString("OOOServer.server-bind-address"),
                                                  ConfigFactory.load.getInt("OOOServer.port")))


    def receive = {
        case b@Bound(localAddress) =>
        // do some logging or setup ...

        case CommandFailed(_: Bind) => context stop self

        case c@Connected(remote, local) =>
            val controller = context.actorOf(Props[Controller])
            val connection = sender()
            connection ! Register(controller)
            controller ! Register(sender())

        case Received(data) =>
            logger.info(s"Received ${data.utf8String}")
            Message.fmtJsonReads.reads(Json.parse({data.utf8String})) match {
                case JsSuccess(msg,_) => msg match {
                    case m : LoginRequest => logger.info("login request arrived")
                        Server.sessions = Server.sessions.+(m.nickname -> sender())
                    case m : LogoutRequest => println("logout request arrived")
                }
                case JsError(e) => logger.warn("unsupported message arrived")
            }
    }

}

object Server {

    final val logger = LoggerFactory.getLogger(this.getClass)

    var sessions : Map[String,ActorRef] = Map()


    implicit val system = ActorSystem("root")

    val zserver = system.actorOf(Props(classOf[Server]))

    def main(args: Array[String]) {
        logger.info("OOOServer is starting...")

        Runtime.getRuntime.addShutdownHook(new Thread() {
            override def run() = {
                zserver ! PoisonPill
                system.shutdown()
            }
        })
    }
}
