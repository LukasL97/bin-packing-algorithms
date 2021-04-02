package actors.dumpers.combining

import actors.BinPackingSolutionStep
import akka.actor.Actor
import dao.CombinedBinPackingSolutionStepDAO
import models.problem.binpacking.BinPackingInstance

object GreedyCombiningSolutionStepDumperProcessor {
  trait Factory {
    def apply(): Actor
  }
}

class GreedyCombiningSolutionStepDumperProcessor(
  override val dao: CombinedBinPackingSolutionStepDAO,
  override val instance: BinPackingInstance
) extends CombiningSolutionStepDumperProcessor {

  private val targetStepCount = 100
  private val combiningInterval = Math.max(Math.round(instance.numRectangles.toDouble / targetStepCount), 1).toInt

  override def popStepsFromQueue(): Seq[BinPackingSolutionStep] = {
    queue.lastOption match {
      case Some(solutionStep) if solutionStep.step == 0 || solutionStep.finished => popNStepsFromQueue(queue.size)
      case Some(_) if queue.size >= combiningInterval => popNStepsFromQueue(combiningInterval)
      case _ => Seq.empty[BinPackingSolutionStep]
    }
  }

  private def popNStepsFromQueue(n: Int): Seq[BinPackingSolutionStep] = {
    val poppedSteps = queue.take(n).toSeq
    queue.remove(0, n)
    poppedSteps
  }
}
