package models.problem.binpacking.greedy.basic

import models.problem.binpacking.solution.Rectangle

class RandomSelectionBinPackingGreedy(
  override val boxLength: Int,
  override val numRectangles: Int,
  override val rectangleWidthRange: (Int, Int),
  override val rectangleHeightRange: (Int, Int)
) extends BasicBinPackingGreedy {

  override val selectionHandler: BasicBinPackingSelectionHandler =
    new RandomSelectionBinPackingSelectionHandler(
      boxLength,
      rectangles
    )
}

class RandomSelectionBinPackingSelectionHandler(
  override val boxLength: Int,
  override val candidates: Iterable[Rectangle]
) extends BasicBinPackingSelectionHandler {

  override def selectNextCandidate(candidates: Iterable[Rectangle]): (Rectangle, Iterable[Rectangle]) = {
    (candidates.head, candidates.tail)
  }
}
