package actors.executors

import actors.BinPackingSolutionStep
import akka.actor.ActorRef
import akka.actor.ActorRef.noSender
import metrics.Metrics
import models.problem.binpacking.greedy.BinPackingGreedy
import models.problem.binpacking.solution.BinPackingSolution
import play.api.Logging

class BinPackingGreedyExecutor(
  val binPacking: BinPackingGreedy[_ <: BinPackingSolution],
  val runId: String,
  val dumpers: Seq[ActorRef]
) extends BinPackingExecutor with Logging with Metrics {

  override def execute(): Unit = {
    withContext("runId" -> runId) {
      withTimer("greedy-run") {
        logger.info(s"Starting ${getClass.getSimpleName} for runId $runId")
        dumpers.foreach(
          _.tell(
            BinPackingSolutionStep.startStep(
              runId,
              binPacking.transformToStoredRepresentation(binPacking.startSolution)
            ),
            noSender
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
    dumpers.foreach(
      _.tell(
        BinPackingSolutionStep(
          runId,
          step,
          binPacking.transformToStoredRepresentation(solution),
          finished
        ),
        noSender
      )
    )
  }
}
