package models.problem.binpacking.greedy

import models.problem.binpacking.BinPackingInstance
import models.problem.binpacking.solution.BoxClosingTopLeftFirstBinPackingSolution
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.utils.RectangleSizeOrdering

class BoxClosingBinPackingGreedy(
  override val instance: BinPackingInstance
) extends BinPackingGreedy[BoxClosingTopLeftFirstBinPackingSolution] with RectangleSizeOrdering {

  override val selectionHandler: BinPackingSelectionHandler[BoxClosingTopLeftFirstBinPackingSolution] =
    new BoxClosingBinPackingSelectionHandler(instance.boxLength, instance.rectangles.sorted.reverse)
}

class BoxClosingBinPackingSelectionHandler(
  val boxLength: Int,
  override val candidates: Seq[Rectangle]
) extends BinPackingSelectionHandler[BoxClosingTopLeftFirstBinPackingSolution] {

  private val maxConsideredCandidatesPerStep = 10

  override val startSolution: BoxClosingTopLeftFirstBinPackingSolution =
    BoxClosingTopLeftFirstBinPackingSolution(boxLength)

  override def selectNextCandidate(
    candidates: Iterable[Rectangle],
    solution: BoxClosingTopLeftFirstBinPackingSolution
  ): (Rectangle, Iterable[Rectangle]) = {
    val consideredCandidateIndexes = getConsideredCandidateIndexes(candidates.size)
    val candidatesSeq = candidates.asInstanceOf[Seq[Rectangle]]
    val openBoxId = solution.closedBoxes.maxOption.getOrElse(0) + 1
    consideredCandidateIndexes.collectFirst {
      case index if {
            val rectangle = candidatesSeq(index)
            solution.placeTopLeftFirstInSpecificBox(rectangle, openBoxId).isDefined
          } =>
        (candidatesSeq(index), candidatesSeq.zipWithIndex.filterNot(_._2 == index).map(_._1))
    }.getOrElse(
      (candidatesSeq.head, candidatesSeq.tail)
    )
  }

  private def getConsideredCandidateIndexes(candidatesSize: Int): Seq[Int] = {
    val stepBetweenIndexes = Math.max((candidatesSize + 1) / (maxConsideredCandidatesPerStep - 1), 1)
    val candidateIndexes = for (i <- 0 until candidatesSize by stepBetweenIndexes) yield i
    if (candidateIndexes.contains(candidatesSize - 1)) {
      candidateIndexes
    } else {
      candidateIndexes.appended(candidatesSize - 1)
    }
  }

  override def placeCandidateInSolution(
    candidate: Rectangle,
    solution: BoxClosingTopLeftFirstBinPackingSolution
  ): BoxClosingTopLeftFirstBinPackingSolution = {
    solution.placeTopLeftFirst(candidate)
  }
}
