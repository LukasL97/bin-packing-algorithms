package actors.dumpers.combining

import actors.BinPackingSolutionStep
import dao.CombinedBinPackingSolutionStepDAO
import models.problem.binpacking.solution.update.RectanglesChanged
import models.problem.binpacking.solution.update.UnchangedSolution

class LocalSearchCombiningSolutionStepDumperProcessor(
  override val dao: CombinedBinPackingSolutionStepDAO
) extends CombiningSolutionStepDumperProcessor {

  private[combining] val minimalChangesCombiningThreshold = 20

  override protected def processQueue(): Unit = {
    require(queue.nonEmpty)
    val onlyMinimalChanges = onlyMinimalChangesInQueue()
    if (onlyMinimalChanges && queue.size >= minimalChangesCombiningThreshold) {
      dao.dumpSolutionStep(combine(queue.toSeq))
      queue.clear()
    } else if (!onlyMinimalChanges) {
      val nonMinimalLastStep = queue.last
      val minimalPreviousSteps = queue.dropRight(1).toSeq
      if (minimalPreviousSteps.nonEmpty) {
        dao.dumpSolutionStep(combine(minimalPreviousSteps))
      }
      dao.dumpSolutionStep(nonMinimalLastStep)
      queue.clear()
    }
  }

  private def onlyMinimalChangesInQueue(): Boolean = queue.forall {
    case BinPackingSolutionStep(_, _, solution, _) if solution.update.isInstanceOf[RectanglesChanged] =>
      solution.update.asInstanceOf[RectanglesChanged].rectangleIds.size == 1
    case BinPackingSolutionStep(_, _, solution, _) if solution.update.isInstanceOf[UnchangedSolution] => true
    case _ => false
  }
}
