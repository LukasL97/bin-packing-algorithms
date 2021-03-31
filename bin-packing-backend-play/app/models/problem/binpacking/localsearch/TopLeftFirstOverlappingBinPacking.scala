package models.problem.binpacking.localsearch

import models.algorithm.OneDimensionalScore
import models.algorithm.Score
import models.problem.binpacking.solution.OverlappingTopLeftFirstBinPackingSolution
import models.problem.binpacking.solution.Rectangle

import scala.collection.View

class TopLeftFirstOverlappingBinPacking(
  override val boxLength: Int,
  override val numRectangles: Int,
  override val rectangleWidthRange: (Int, Int),
  override val rectangleHeightRange: (Int, Int)
) extends BinPackingLocalSearch[OverlappingTopLeftFirstBinPackingSolution] {

  override val solutionHandler: BinPackingSolutionHandler[OverlappingTopLeftFirstBinPackingSolution] =
    new TopLeftFirstOverlappingBinPackingSolutionHandler(rectangles, boxLength)
}

class TopLeftFirstOverlappingBinPackingSolutionHandler(
  rectangles: Set[Rectangle],
  boxLength: Int
) extends BinPackingSolutionHandler[OverlappingTopLeftFirstBinPackingSolution] {

  override val startSolution: OverlappingTopLeftFirstBinPackingSolution =
    OverlappingTopLeftFirstBinPackingSolution.apply(rectangles.toSeq, boxLength, 1.0)

  override def getNeighborhood(
    solution: OverlappingTopLeftFirstBinPackingSolution,
    step: Int
  ): View[OverlappingTopLeftFirstBinPackingSolution] = View.empty

  override def evaluate(solution: OverlappingTopLeftFirstBinPackingSolution, step: Int): Score = {
    OneDimensionalScore(0)
  }
}
