package actors.dumpers.combining

import actors.BinPackingSolutionStep
import dao.CombinedBinPackingSolutionStepDAO
import models.problem.binpacking.BinPackingInstance

import scala.collection.mutable

trait CombiningSolutionStepDumperProcessor {

  val dao: CombinedBinPackingSolutionStepDAO
  val instance: BinPackingInstance

  protected[combining] val queue: mutable.Buffer[BinPackingSolutionStep] = mutable.Buffer.empty[BinPackingSolutionStep]

  def process(solutionStep: BinPackingSolutionStep): Unit = {
    queue.append(solutionStep)
    val steps = popStepsFromQueue()
    if (steps.nonEmpty) {
      dao.dumpSolutionStep(combine(steps))
    }
  }

  protected def popStepsFromQueue(): Seq[BinPackingSolutionStep]

  protected def combine(solutionSteps: Seq[BinPackingSolutionStep]): BinPackingSolutionStep = {
    solutionSteps.lastOption.getOrElse(throw new RuntimeException("Tried combining empty list of solution steps"))
  }
}
