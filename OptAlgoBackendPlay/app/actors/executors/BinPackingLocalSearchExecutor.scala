package actors.executors

import actors.BinPackingSolutionStep
import dao.BinPackingSolutionStepDAO
import models.problem.binpacking.BinPackingSolution
import models.problem.binpacking.localsearch.BinPackingLocalSearch
import play.api.Logging

class BinPackingLocalSearchExecutor(dao: BinPackingSolutionStepDAO)
    extends BinPackingExecutor[BinPackingLocalSearch] with Logging {

  // TODO: proper configuration
  val maxIterations = 1000

  override def execute(runId: String, binPacking: BinPackingLocalSearch): Unit = {
    logger.info(s"Starting ${getClass.getSimpleName} for runId $runId")
    dao.dumpSolutionStep(
      BinPackingSolutionStep.startStep(
        runId,
        binPacking.localSearch.startSolution
      )
    )
    binPacking.localSearch.run(maxIterations, dumpSolutionStep(runId))
    logger.info(s"Finished ${getClass.getSimpleName} for runId $runId")
  }

  private def dumpSolutionStep(
    runId: String
  )(solution: BinPackingSolution, step: Int, finished: Boolean): Unit = {
    logger.trace(s"Dumping solution step $step for runId $runId")
    dao.dumpSolutionStep(
      BinPackingSolutionStep(
        runId,
        step,
        solution,
        finished || (step == maxIterations)
      )
    )
  }
}
