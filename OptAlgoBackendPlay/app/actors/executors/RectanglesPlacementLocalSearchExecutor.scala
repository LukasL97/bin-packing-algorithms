package actors.executors

import actors.RectanglesPlacementSolutionStep
import dao.RectanglesPlacementSolutionStepDAO
import models.problem.rectangles.RectanglesPlacementSolution
import models.problem.rectangles.localsearch.RectanglesPlacementLocalSearch
import play.api.Logging

class RectanglesPlacementLocalSearchExecutor(dao: RectanglesPlacementSolutionStepDAO)
    extends RectanglesPlacementExecutor[RectanglesPlacementLocalSearch] with Logging {

  // TODO: proper configuration
  val maxIterations = 1000

  override def execute(runId: String, rectanglesPlacement: RectanglesPlacementLocalSearch): Unit = {
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

  private def dumpSolutionStep(
    runId: String
  )(solution: RectanglesPlacementSolution, step: Int, finished: Boolean): Unit = {
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
