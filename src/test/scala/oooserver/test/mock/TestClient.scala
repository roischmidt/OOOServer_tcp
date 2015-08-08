package oooserver.test.mock

import akka.actor.{Props, ActorSystem, Actor}
import akka.actor.Actor.Receive
import akka.io.Tcp.Received
import org.slf4j.LoggerFactory

/**
 * Created by rois on 7/15/15.
 */
class TestClient extends Actor{

    final val logger = LoggerFactory.getLogger(this.getClass)

    override def receive: Receive = {
        case Received(data) =>
            logger.info(s"Controller Received ${data.utf8String} , sender: ${sender}")
    }
}

object TestClient {

    implicit val system = ActorSystem("test")

    lazy val zserver = system.actorOf(Props(classOf[TestClient]))
}
