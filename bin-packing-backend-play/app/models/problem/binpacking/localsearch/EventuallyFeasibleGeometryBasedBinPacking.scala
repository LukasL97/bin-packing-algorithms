package models.problem.binpacking.localsearch
import models.algorithm.OneDimensionalScore
import models.algorithm.Score
import models.problem.binpacking.localsearch.evaluation.BoxWeightedTopLeftFirstEvaluation
import models.problem.binpacking.localsearch.evaluation.OverlapPenalization
import models.problem.binpacking.localsearch.neighborhood.Down
import models.problem.binpacking.localsearch.neighborhood.GeometricShiftNeighborhood
import models.problem.binpacking.localsearch.neighborhood.Left
import models.problem.binpacking.localsearch.neighborhood.Right
import models.problem.binpacking.localsearch.neighborhood.Up
import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.SimpleBinPackingSolution

class EventuallyFeasibleGeometryBasedBinPacking(
  override val boxLength: Int,
  override val numRectangles: Int,
  override val rectangleWidthRange: (Int, Int),
  override val rectangleHeightRange: (Int, Int)
) extends BinPackingLocalSearch {

  override val solutionHandler: BinPackingSolutionHandler =
    new EventuallyFeasibleGeometryBasedBinPackingSolutionHandler(
      rectangles,
      boxLength
    )

}

class EventuallyFeasibleGeometryBasedBinPackingSolutionHandler(
  val rectangles: Set[Rectangle],
  val boxLength: Int,
) extends BinPackingSolutionHandler with GeometricShiftNeighborhood with BoxWeightedTopLeftFirstEvaluation
    with OverlapPenalization {

  override val startSolution: BinPackingSolution = SimpleBinPackingSolution(
    rectangles
      .map(rectangle => rectangle -> Placing(Box(1, boxLength), Coordinates(0, 0)))
      .toMap
  )

  override def getNeighborhood(solution: BinPackingSolution): Set[BinPackingSolution] = {
    val solutionsWithLeftShift = createShiftedSolutions(solution, Left, 1, allowOverlap = true)
    val solutionsWithRightShift = createShiftedSolutions(solution, Right, 1, allowOverlap = true)
    val solutionsWithUpShift = createShiftedSolutions(solution, Up, 1, allowOverlap = true)
    val solutionsWithDownShift = createShiftedSolutions(solution, Down, 1, allowOverlap = true)
    solutionsWithLeftShift ++ solutionsWithRightShift ++ solutionsWithUpShift ++ solutionsWithDownShift
  }

  def maxAllowedOverlap(step: Int): Double = {
    0
  }

  override def evaluate(solution: BinPackingSolution, step: Int): Score = {
    val overlapPenalization = penalizeOverlap(solution, maxAllowedOverlap(step))
    PrioritizedPenalizationScore(
      OneDimensionalScore(evaluate(solution)),
      OneDimensionalScore(overlapPenalization)
    )
  }

  override def stopOnStagnation(solution: BinPackingSolution): Boolean = {
    isFeasible(solution)
  }
}

case class PrioritizedPenalizationScore(
  actualObjectiveScore: Score,
  penalization: Score
) extends Score {
  override def compare(that: Score): Int = {
    val other = that.asInstanceOf[PrioritizedPenalizationScore]
    penalization.compareTo(other.penalization) match {
      case 0 => actualObjectiveScore.compareTo(other.actualObjectiveScore)
      case result => result
    }
  }
}
