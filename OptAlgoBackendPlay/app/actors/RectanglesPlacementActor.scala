package actors

import akka.actor.Actor
import com.google.inject.Inject
import dao.RectanglesPlacementSolutionStepDAO
import models.problem.rectangles.RectanglesPlacement
import models.problem.rectangles.RectanglesPlacementSolution
import play.api.Logging

object RectanglesPlacementActor {
  trait Factory {
    def apply(): Actor
  }
}

class RectanglesPlacementActor @Inject()(val dao: RectanglesPlacementSolutionStepDAO) extends Actor {
  override def receive: Receive = {
    case (runId: String, rectanglesPlacement: RectanglesPlacement) =>
      val executor = new RectanglesPlacementExecutor(dao)
      executor.execute(runId, rectanglesPlacement)
  }
}

class RectanglesPlacementExecutor(dao: RectanglesPlacementSolutionStepDAO) extends Logging {

  // TODO: proper configuration
  val maxIterations = 1000

  def execute(runId: String, rectanglesPlacement: RectanglesPlacement): Unit = {
    logger.info(s"Starting ${getClass.getSimpleName} for runId $runId")
    dao.dumpSolutionStep(
      RectanglesPlacementSolutionStep.getStartStep(
        runId,
        rectanglesPlacement.localSearch.startSolution
      )
    )
    rectanglesPlacement.localSearch.run(maxIterations, dumpSolutionStep(runId))
    logger.info(s"Finished ${getClass.getSimpleName} for runId $runId")
  }

  private def dumpSolutionStep(runId: String)(solution: RectanglesPlacementSolution, step: Int, finished: Boolean): Unit = {
    logger.trace(s"Dumping solution step $step for runId $runId")
    dao.dumpSolutionStep(
      RectanglesPlacementSolutionStep(
        runId,
        step,
        solution,
        finished || (step == maxIterations)
      )
    )
  }
}


