package models.problem.binpacking.localsearch

import models.algorithm.OneDimensionalScore
import models.algorithm.Score
import models.problem.binpacking.localsearch.evaluation.BoxWeightedScore
import models.problem.binpacking.localsearch.evaluation.BoxWeightedTopLeftFirstEvaluation
import models.problem.binpacking.localsearch.neighborhood.ExceededOverlapOutsourcingNeighborhood
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
  override val boxLength: Int
) extends BinPackingSolutionHandler[OverlappingTopLeftFirstBinPackingSolution]
    with BoxWeightedTopLeftFirstEvaluation {

  override val startSolution: OverlappingTopLeftFirstBinPackingSolution =
    OverlappingTopLeftFirstBinPackingSolution.apply(rectangles.toSeq, boxLength, 1.0)

  private val exceededOverlapOutsourcingNeighborhood = new ExceededOverlapOutsourcingNeighborhood(boxLength)

  override def getNeighborhood(
    solution: OverlappingTopLeftFirstBinPackingSolution,
    step: Int
  ): View[OverlappingTopLeftFirstBinPackingSolution] = {
    val solutionsWithOutsourcedRectangles = exceededOverlapOutsourcingNeighborhood
      .createExceededOverlapOutsourcingNeighborhood(solution, maxAllowedOverlap(step))
    solutionsWithOutsourcedRectangles
  }

  override def evaluate(solution: OverlappingTopLeftFirstBinPackingSolution, step: Int): Score = {
    OverlappingBoxWeightedScore(
      evaluate(solution),
      OneDimensionalScore(solution.getExceededOverlapCount(maxAllowedOverlap(step)))
    )
  }

  private def maxAllowedOverlap(step: Int): Double = {
    Math.max(1.0 - step * 0.01, 0.0)
  }

  override def stopOnStagnation(solution: OverlappingTopLeftFirstBinPackingSolution): Boolean = {
    solution.overlappings.forall {
      case (_, boxOverlappings) => boxOverlappings.isEmpty
    }
  }
}

case class OverlappingBoxWeightedScore(
  objectiveScore: BoxWeightedScore,
  overlapPenalty: OneDimensionalScore
) extends Score {
  override def compare(that: Score): Int = {
    val other = that.asInstanceOf[OverlappingBoxWeightedScore]
    overlapPenalty.compareTo(other.overlapPenalty) match {
      case 0 => objectiveScore.compareTo(other.objectiveScore)
      case result => result
    }
  }
}
