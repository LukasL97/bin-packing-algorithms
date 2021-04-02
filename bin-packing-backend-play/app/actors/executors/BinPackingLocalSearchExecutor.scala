package actors.executors

import actors.BinPackingSolutionStep
import akka.actor.ActorRef
import akka.actor.ActorRef.noSender
import metrics.Metrics
import models.problem.binpacking.localsearch.BinPackingLocalSearch
import models.problem.binpacking.solution.BinPackingSolution
import play.api.Logging

class BinPackingLocalSearchExecutor(
  val binPacking: BinPackingLocalSearch[_ <: BinPackingSolution],
  val runId: String,
  val dumper: ActorRef,
  val timeLimit: Option[Int]
) extends BinPackingExecutor with Logging with Metrics {

  // TODO: proper configuration
  val maxIterations = 10000

  override def execute(): Unit = {
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
        binPacking.localSearch.run(maxIterations, timeLimit, dumpSolutionStep(runId))
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
        finished
      ),
      noSender
    )
  }
}
