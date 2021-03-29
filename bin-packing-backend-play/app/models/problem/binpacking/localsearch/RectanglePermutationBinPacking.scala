package models.problem.binpacking.localsearch

import models.algorithm.Score
import models.problem.binpacking.localsearch.evaluation.BoxWeightedTopLeftFirstEvaluation
import models.problem.binpacking.solution.BoxClosingTopLeftFirstBinPackingSolution
import models.problem.binpacking.solution.Rectangle

import scala.collection.View

class RectanglePermutationBinPacking(
  override val boxLength: Int,
  override val numRectangles: Int,
  override val rectangleWidthRange: (Int, Int),
  override val rectangleHeightRange: (Int, Int)
) extends BinPackingLocalSearch[BoxClosingTopLeftFirstBinPackingSolution] {

  override val solutionHandler: BinPackingSolutionHandler[BoxClosingTopLeftFirstBinPackingSolution] =
    new RectanglePermutationBinPackingSolutionHandler(rectangles.toSeq, boxLength)
}

class RectanglePermutationBinPackingSolutionHandler(
  rectangles: Seq[Rectangle],
  override val boxLength: Int
) extends BinPackingSolutionHandler[BoxClosingTopLeftFirstBinPackingSolution] with BoxWeightedTopLeftFirstEvaluation {

  override val startSolution: BoxClosingTopLeftFirstBinPackingSolution =
    BoxClosingTopLeftFirstBinPackingSolution(rectangles, boxLength)

  override def getNeighborhood(
    solution: BoxClosingTopLeftFirstBinPackingSolution
  ): View[BoxClosingTopLeftFirstBinPackingSolution] = View.empty

  override def evaluate(solution: BoxClosingTopLeftFirstBinPackingSolution, step: Int): Score = {
    evaluate(solution)
  }
}
