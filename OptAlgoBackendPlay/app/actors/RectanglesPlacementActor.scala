package actors

import actors.executors.RectanglesPlacementGreedyExecutor
import actors.executors.RectanglesPlacementLocalSearchExecutor
import akka.actor.Actor
import com.google.inject.Inject
import dao.RectanglesPlacementSolutionStepDAO
import models.problem.rectangles.greedy.RectanglesPlacementGreedy
import models.problem.rectangles.localsearch.RectanglesPlacementLocalSearch

object RectanglesPlacementActor {
  trait Factory {
    def apply(): Actor
  }
}

class RectanglesPlacementActor @Inject()(val dao: RectanglesPlacementSolutionStepDAO) extends Actor {
  override def receive: Receive = {
    case (runId: String, rectanglesPlacement: RectanglesPlacementLocalSearch) =>
      val executor = new RectanglesPlacementLocalSearchExecutor(dao)
      executor.execute(runId, rectanglesPlacement)
    case (runId: String, rectanglesPlacement: RectanglesPlacementGreedy) =>
      val executor = new RectanglesPlacementGreedyExecutor(dao)
      executor.execute(runId, rectanglesPlacement)
  }
}
