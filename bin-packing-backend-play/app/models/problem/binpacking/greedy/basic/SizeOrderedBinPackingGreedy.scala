package models.problem.binpacking.greedy.basic

import models.problem.binpacking.BinPackingInstance
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.SimpleBinPackingSolution
import models.problem.binpacking.utils.RectangleSizeOrdering

class SizeOrderedBinPackingGreedy(
  override val instance: BinPackingInstance
) extends BasicBinPackingGreedy with RectangleSizeOrdering {

  override val selectionHandler: BasicBinPackingSelectionHandler = new SizeOrderedBinPackingSelectionHandler(
    instance.boxLength,
    instance.rectangles.sorted.reverse
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
