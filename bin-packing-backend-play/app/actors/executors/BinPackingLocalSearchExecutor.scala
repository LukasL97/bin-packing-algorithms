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
  val dumpers: Seq[ActorRef],
  val timeLimit: Option[Int]
) extends BinPackingExecutor with Logging with Metrics {

  // TODO: proper configuration
  val maxIterations = 10000

  override def execute(): Unit = {
    withContext("runId" -> runId) {
      withTimer("local-search-run") {
        logger.info(s"Starting ${getClass.getSimpleName} for runId $runId")
        dumpers.foreach(
          _.tell(
            BinPackingSolutionStep.startStep(
              runId,
              binPacking.solutionHandler.startSolution.asSimpleSolution
            ),
            noSender
          )
        )
        binPacking.localSearch.run(maxIterations, timeLimit, dumpSolutionStep(runId))
        logger.info(s"Finished ${getClass.getSimpleName} for runId $runId")
      }
    }
  }

  private def dumpSolutionStep(
    runId: String
  )(solution: BinPackingSolution, step: Int, finished: Boolean): Unit = {
    dumpers.foreach(
      _.tell(
        BinPackingSolutionStep(
          runId,
          step,
          solution.asSimpleSolution,
          finished
        ),
        noSender
      )
    )
  }
}
