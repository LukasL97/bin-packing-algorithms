package actors.executors

import actors.RectanglesPlacementSolutionStep
import dao.RectanglesPlacementSolutionStepDAO
import models.problem.rectangles.RectanglesPlacementSolution
import models.problem.rectangles.greedy.RectanglesPlacementGreedy
import play.api.Logging

class RectanglesPlacementGreedyExecutor(dao: RectanglesPlacementSolutionStepDAO)
    extends RectanglesPlacementExecutor[RectanglesPlacementGreedy] with Logging {

  override def execute(runId: String, rectanglesPlacement: RectanglesPlacementGreedy): Unit = {
    logger.info(s"Starting ${getClass.getSimpleName} for runId $runId")
    dao.dumpSolutionStep(
      RectanglesPlacementSolutionStep.getStartStep(
        runId,
        rectanglesPlacement.selectionHandler.startSolution
      )
    )
    rectanglesPlacement.greedy.run(dumpSolutionStep(runId))
  }

  private def dumpSolutionStep(
    runId: String
  )(solution: RectanglesPlacementSolution, step: Int, finished: Boolean): Unit = {
    logger.trace(s"Dumping solution step $step for runId $runId")
    dao.dumpSolutionStep(
      RectanglesPlacementSolutionStep(
        runId,
        step,
        solution,
        finished
      )
    )
  }
}
