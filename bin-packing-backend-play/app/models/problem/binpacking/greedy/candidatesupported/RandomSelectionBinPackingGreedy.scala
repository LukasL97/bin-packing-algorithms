package models.problem.binpacking.greedy.candidatesupported

import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.TopLeftFirstBinPackingSolution

class RandomSelectionBinPackingGreedy(
  override val boxLength: Int,
  override val numRectangles: Int,
  override val rectangleWidthRange: (Int, Int),
  override val rectangleHeightRange: (Int, Int)
) extends CandidateSupportedBinPackingGreedy {

  override val selectionHandler: CandidateSupportedBinPackingSelectionHandler =
    new RandomSelectionBinPackingSelectionHandler(
      boxLength,
      rectangles
    )
}

class RandomSelectionBinPackingSelectionHandler(
  override val boxLength: Int,
  override val candidates: Iterable[Rectangle]
) extends CandidateSupportedBinPackingSelectionHandler {

  override def selectNextCandidate(
    candidates: Iterable[Rectangle],
    solution: TopLeftFirstBinPackingSolution
  ): (Rectangle, Iterable[Rectangle]) = {
    (candidates.head, candidates.tail)
  }
}
