package models.problem.binpacking.greedy.basic

import models.problem.binpacking.BinPackingInstance
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.SimpleBinPackingSolution

class RandomSelectionBinPackingGreedy(
  override val instance: BinPackingInstance
) extends BasicBinPackingGreedy {

  override val selectionHandler: BasicBinPackingSelectionHandler =
    new RandomSelectionBinPackingSelectionHandler(
      instance.boxLength,
      instance.rectangles
    )
}

class RandomSelectionBinPackingSelectionHandler(
  override val boxLength: Int,
  override val candidates: Iterable[Rectangle]
) extends BasicBinPackingSelectionHandler {

  override def selectNextCandidate(
    candidates: Iterable[Rectangle],
    solution: SimpleBinPackingSolution
  ): (Rectangle, Iterable[Rectangle]) = {
    (candidates.head, candidates.tail)
  }
}
