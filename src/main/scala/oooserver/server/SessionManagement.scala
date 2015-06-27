package oooserver.server

import akka.actor.ActorRef

import scala.collection.mutable.HashMap

object SessionManager {

    private val sessions = new HashMap[String, ActorRef]

    def add(username: String,con: ActorRef) = sessions += (username -> con)

    def get(username: String) : Option[ActorRef] = sessions.get(username)

    def remove(username: String) = sessions -= username

}