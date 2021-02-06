package actors

import akka.actor.Actor
import akka.actor.Props
import models.problem.rectangles.RectanglesPlacement

object RectanglesPlacementExecutor {
  def props: Props = Props[RectanglesPlacementExecutor]()
}

class RectanglesPlacementExecutor extends Actor {

  override def receive: Receive = {
    case (runId: String, rectanglesPlacement: RectanglesPlacement) => execute(runId, rectanglesPlacement)
  }

  private def execute(runId: String, rectanglesPlacement: RectanglesPlacement): Unit = {
    println(s"Starting ${getClass.getSimpleName} for runId $runId")
  }
}
