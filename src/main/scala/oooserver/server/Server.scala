package oooserver.server

import java.net.InetSocketAddress

import akka.actor.{Props, Actor, PoisonPill, ActorSystem}
import akka.io.{Tcp, IO}
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

object Server {

	final val logger = LoggerFactory.getLogger(this.getClass)

	implicit val system = ActorSystem("root")

	val serverBindAddress = ConfigFactory.load.getString("OOOServer.server-bind-address")
	val port = ConfigFactory.load.getString("OOOServer.port").toInt

	def main(args: Array[String]) {
		val zserver = system.actorOf(Props(classOf[Controller]))
		IO(Tcp) ! Tcp.Bind(zserver, new InetSocketAddress(serverBindAddress,port))
		logger.info(s"server has started on port:$port")

		Runtime.getRuntime.addShutdownHook(new Thread() {
			override def run() = {
				zserver ! PoisonPill
				system.shutdown()
			}
		})
	}



}
