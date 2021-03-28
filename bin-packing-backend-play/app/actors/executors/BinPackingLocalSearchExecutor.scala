package actors.executors

import actors.BinPackingSolutionStep
import akka.actor.ActorRef
import akka.actor.ActorRef.noSender
import metrics.Metrics
import models.problem.binpacking.localsearch.BinPackingLocalSearch
import models.problem.binpacking.solution.BinPackingSolution
import play.api.Logging

class BinPackingLocalSearchExecutor[A <: BinPackingSolution](dumper: ActorRef)
    extends BinPackingExecutor[BinPackingLocalSearch[A]] with Logging with Metrics {

  // TODO: proper configuration
  val maxIterations = 10000

  override def execute(runId: String, binPacking: BinPackingLocalSearch[A]): Unit = {
    withContext("runId" -> runId) {
      withTimer("local-search-run") {
        logger.info(s"Starting ${getClass.getSimpleName} for runId $runId")
        dumper.tell(
          BinPackingSolutionStep.startStep(
            runId,
            binPacking.solutionHandler.startSolution.asSimpleSolution
          ),
          noSender
        )
        binPacking.localSearch.run(maxIterations, dumpSolutionStep(runId))
        logger.info(s"Finished ${getClass.getSimpleName} for runId $runId")
      }
    }
  }

  private def dumpSolutionStep(
    runId: String
  )(solution: BinPackingSolution, step: Int, finished: Boolean): Unit = {
    dumper.tell(
      BinPackingSolutionStep(
        runId,
        step,
        solution.asSimpleSolution,
        finished || (step == maxIterations)
      ),
      noSender
    )
  }
}
