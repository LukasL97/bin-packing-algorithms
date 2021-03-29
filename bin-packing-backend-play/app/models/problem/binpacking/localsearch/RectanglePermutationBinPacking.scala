package models.problem.binpacking.localsearch

import models.algorithm.Score
import models.problem.binpacking.localsearch.evaluation.BoxWeightedTopLeftFirstEvaluation
import models.problem.binpacking.localsearch.neighborhood.RectanglePermutationNeighborhood
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

  private val neighborhood = new RectanglePermutationNeighborhood(
    boxLength,
    targetBoxFillGrade = 1.0,
    consideredCandidatesAreaOvershootFactor = 2.0,
    consideredCandidatesAreaMaximumRelativeSize = 0.3
  )

  override def getNeighborhood(
    solution: BoxClosingTopLeftFirstBinPackingSolution
  ): View[BoxClosingTopLeftFirstBinPackingSolution] = {
    neighborhood.createRectanglePermutationNeighborhood(solution)
  }

  override def evaluate(solution: BoxClosingTopLeftFirstBinPackingSolution, step: Int): Score = {
    evaluate(solution)
  }
}