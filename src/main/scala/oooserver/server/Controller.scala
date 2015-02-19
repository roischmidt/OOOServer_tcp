package oooserver.server

import akka.actor.Actor
import akka.io.Tcp
import akka.io.Tcp._
import akka.util.ByteString
import org.slf4j.LoggerFactory

/**
 * Created by rois on 2/19/15.
 */
class Controller extends Actor{

	final val logger = LoggerFactory.getLogger(this.getClass)

	override def receive: Receive = {
//		case _: Tcp.Connected => sender ! Tcp.Register(self)
//
//		case x: Tcp.Message =>
//		logger.info(x.)
//		sender ! "Hello"
		case CommandFailed(_: Connect) =>
			logger.info("connect failed")
			context stop self

		case c @ Connected(remote, local) =>
			logger.info(c.toString)
			//val zsender = sender()
			sender ! Register(self)
			context become {
				case data: ByteString =>
					sender() ! Write(data)
				case CommandFailed(w: Write) =>
					// O/S buffer was full
					logger.info("write failed")
				case Received(data) =>
					logger.info(data.utf8String)
					sender() ! "Welcome"
				case "close" =>
					sender() ! Close
				case _: ConnectionClosed =>
					logger.info("connection closed")
					context stop self
			}
	}
}
