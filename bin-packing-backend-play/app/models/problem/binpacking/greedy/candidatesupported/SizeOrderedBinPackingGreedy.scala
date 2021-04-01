package models.problem.binpacking.greedy.candidatesupported

import models.problem.binpacking.BinPackingInstance
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.TopLeftFirstBinPackingSolution
import models.problem.binpacking.utils.RectangleSizeOrdering

class SizeOrderedBinPackingGreedy(
  override val instance: BinPackingInstance
) extends CandidateSupportedBinPackingGreedy with RectangleSizeOrdering {

  override val selectionHandler: CandidateSupportedBinPackingSelectionHandler =
    new SizeOrderedBinPackingSelectionHandler(
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
) extends CandidateSupportedBinPackingSelectionHandler {

  override def selectNextCandidate(
    candidates: Iterable[Rectangle],
    solution: TopLeftFirstBinPackingSolution
  ): (Rectangle, Iterable[Rectangle]) = {
    (candidates.head, candidates.tail)
  }

}
