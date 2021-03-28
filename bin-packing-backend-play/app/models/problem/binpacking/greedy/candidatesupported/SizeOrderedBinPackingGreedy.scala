package models.problem.binpacking.greedy.candidatesupported

import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.TopLeftFirstBinPackingSolution
import models.problem.binpacking.utils.RectangleSizeOrdering

class SizeOrderedBinPackingGreedy(
  override val boxLength: Int,
  override val numRectangles: Int,
  override val rectangleWidthRange: (Int, Int),
  override val rectangleHeightRange: (Int, Int)
) extends CandidateSupportedBinPackingGreedy with RectangleSizeOrdering {

  override val selectionHandler: CandidateSupportedBinPackingSelectionHandler =
    new SizeOrderedBinPackingSelectionHandler(
      boxLength,
      rectangles.toSeq.sorted.reverse
    )
}

/**
  * Select candidates ordered by their size (area) in descending order
  */
class SizeOrderedBinPackingSelectionHandler(
  override val boxLength: Int,
  override val candidates: Seq[Rectangle]
) extends CandidateSupportedBinPackingSelectionHandler {

  override def selectNextCandidate(
    candidates: Iterable[Rectangle],
    solution: TopLeftFirstBinPackingSolution
  ): (Rectangle, Iterable[Rectangle]) = {
    (candidates.head, candidates.tail)
  }

}
