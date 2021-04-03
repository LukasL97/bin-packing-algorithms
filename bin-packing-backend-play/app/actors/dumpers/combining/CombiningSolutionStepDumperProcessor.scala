package actors.dumpers.combining

import actors.BinPackingSolutionStep
import dao.CombinedBinPackingSolutionStepDAO
import models.problem.binpacking.solution.update.RectanglesChanged
import models.problem.binpacking.solution.update.UnchangedSolution
import models.problem.binpacking.solution.update.Update

import scala.collection.mutable

trait CombiningSolutionStepDumperProcessor {

  val dao: CombinedBinPackingSolutionStepDAO

  protected[combining] val queue: mutable.Buffer[BinPackingSolutionStep] = mutable.Buffer.empty[BinPackingSolutionStep]

  def process(solutionStep: BinPackingSolutionStep): Unit = {
    if (solutionStep.step == 0 || solutionStep.finished) {
      dao.dumpSolutionStep(solutionStep)
    } else {
      queue.append(solutionStep)
      processQueue()
    }
  }

  protected def processQueue(): Unit

  protected def combine(solutionSteps: Seq[BinPackingSolutionStep]): BinPackingSolutionStep = {
    val update = combineUpdates(solutionSteps.map(_.solution.update))
    val lastSolutionStep = solutionSteps.lastOption
      .getOrElse(throw new RuntimeException("Tried combining empty list of solution steps"))
    lastSolutionStep.copy(solution = lastSolutionStep.solution.setUpdate(update))
  }

  private def combineUpdates(updates: Seq[Update]): Update = {
    updates.foldLeft(UnchangedSolution(): Update) {
      case (UnchangedSolution(), update) => update
      case (RectanglesChanged(ids), UnchangedSolution()) => RectanglesChanged(ids)
      case (RectanglesChanged(ids), RectanglesChanged(newIds)) if newIds.size == 1 => RectanglesChanged(ids ++ newIds)
      case (_, update) => throw new IllegalArgumentException(s"Used illegal update $update in combination")
    }
  }
}
