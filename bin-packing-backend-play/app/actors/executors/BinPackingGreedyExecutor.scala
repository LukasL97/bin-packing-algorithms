package actors.executors

import actors.BinPackingSolutionStep
import dao.BinPackingSolutionStepDAO
import metrics.Metrics
import models.problem.binpacking.greedy.BinPackingGreedy
import models.problem.binpacking.solution.BinPackingSolution
import play.api.Logging

class BinPackingGreedyExecutor(dao: BinPackingSolutionStepDAO)
    extends BinPackingExecutor[BinPackingGreedy] with Logging with Metrics {

  override def execute(runId: String, binPacking: BinPackingGreedy): Unit = {
    withContext("runId" -> runId) {
      withTimer("greedy-run") {
        logger.info(s"Starting ${getClass.getSimpleName} for runId $runId")
        dao.dumpSolutionStep(
          BinPackingSolutionStep.startStep(
            runId,
            binPacking.selectionHandler.startSolution
          )
        )
        binPacking.greedy.run(dumpSolutionStep(runId))
        logger.info(s"Finished ${getClass.getSimpleName} for runId $runId")
      }
    }
  }

  private def dumpSolutionStep(
    runId: String
  )(solution: BinPackingSolution, step: Int, finished: Boolean): Unit = {
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
