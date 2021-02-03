package actors

import akka.actor.{Actor, Props}

object RectanglesPlacementExecutor {
  def props = Props[RectanglesPlacementExecutor]
}

class RectanglesPlacementExecutor extends Actor {
  override def receive: Receive = {
    case message => println(message)
  }
}
