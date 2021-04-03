package actors.dumpers.combining

import actors.BinPackingSolutionStep
import dao.CombinedBinPackingSolutionStepDAO
import models.problem.binpacking.BinPackingInstance

class GreedyCombiningSolutionStepDumperProcessor(
  override val dao: CombinedBinPackingSolutionStepDAO,
  val instance: BinPackingInstance
) extends CombiningSolutionStepDumperProcessor {

  private val targetStepCount = 100
  private val combiningInterval = Math.max(Math.round(instance.numRectangles.toDouble / targetStepCount), 1).toInt

  override protected def processQueue(): Unit = {
    val steps = popStepsFromQueue()
    if (steps.nonEmpty) {
      dao.dumpSolutionStep(combine(steps))
    }
  }

  private[combining] def popStepsFromQueue(): Seq[BinPackingSolutionStep] = {
    if (queue.size >= combiningInterval) {
      popNStepsFromQueue(combiningInterval)
    } else {
      Seq.empty[BinPackingSolutionStep]
    }
  }

  private def popNStepsFromQueue(n: Int): Seq[BinPackingSolutionStep] = {
    val poppedSteps = queue.take(n).toSeq
    queue.remove(0, n)
    poppedSteps
  }
}
