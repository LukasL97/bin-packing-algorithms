package actors.executors

import actors.BinPackingSolutionStep
import dao.BinPackingSolutionStepDAO
import models.problem.binpacking.BinPackingSolution
import models.problem.binpacking.greedy.BinPackingGreedy
import play.api.Logging

class BinPackingGreedyExecutor(dao: BinPackingSolutionStepDAO)
    extends BinPackingExecutor[BinPackingGreedy] with Logging {

  override def execute(runId: String, binPacking: BinPackingGreedy): Unit = {
    logger.info(s"Starting ${getClass.getSimpleName} for runId $runId")
    dao.dumpSolutionStep(
      BinPackingSolutionStep.startStep(
        runId,
        binPacking.selectionHandler.startSolution
      )
    )
    binPacking.greedy.run(dumpSolutionStep(runId))
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
        finished
      )
    )
  }
}
