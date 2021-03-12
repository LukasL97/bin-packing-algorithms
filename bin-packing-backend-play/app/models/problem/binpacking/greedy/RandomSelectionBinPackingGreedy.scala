package models.problem.binpacking.greedy

import models.problem.binpacking.solution.Rectangle

class RandomSelectionBinPackingGreedy(
  override val boxLength: Int,
  override val numRectangles: Int,
  override val rectangleWidthRange: (Int, Int),
  override val rectangleHeightRange: (Int, Int)
) extends BinPackingGreedy {

  override val selectionHandler: BinPackingSelectionHandler =
    new RandomSelectionBinPackingSelectionHandler(
      boxLength,
      rectangles
    )
}

class RandomSelectionBinPackingSelectionHandler(
  override val boxLength: Int,
  override val candidates: Iterable[Rectangle]
) extends BinPackingSelectionHandler {

  override def selectNextCandidate(candidates: Iterable[Rectangle]): (Rectangle, Iterable[Rectangle]) = {
    (candidates.head, candidates.tail)
  }
}
