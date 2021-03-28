package models.problem.binpacking.greedy.basic

import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.SimpleBinPackingSolution
import models.problem.binpacking.utils.RectangleSizeOrdering

class SizeOrderedBinPackingGreedy(
  override val boxLength: Int,
  override val numRectangles: Int,
  override val rectangleWidthRange: (Int, Int),
  override val rectangleHeightRange: (Int, Int)
) extends BasicBinPackingGreedy with RectangleSizeOrdering {

  override val selectionHandler: BasicBinPackingSelectionHandler = new SizeOrderedBinPackingSelectionHandler(
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
) extends BasicBinPackingSelectionHandler {

  override def selectNextCandidate(
    candidates: Iterable[Rectangle],
    solution: SimpleBinPackingSolution
  ): (Rectangle, Iterable[Rectangle]) = {
    (candidates.head, candidates.tail)
  }

}
