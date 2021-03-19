package models.problem.binpacking.localsearch
import models.algorithm.OneDimensionalScore
import models.algorithm.Score
import models.problem.binpacking.localsearch.evaluation.BoxWeightedTopLeftFirstEvaluation
import models.problem.binpacking.localsearch.evaluation.OverlapPenalization
import models.problem.binpacking.localsearch.initialization.GeometricStartSolution
import models.problem.binpacking.localsearch.neighborhood.BoxPullUpNeighborhood
import models.problem.binpacking.localsearch.neighborhood.Down
import models.problem.binpacking.localsearch.neighborhood.GeometricShiftNeighborhood
import models.problem.binpacking.localsearch.neighborhood.Left
import models.problem.binpacking.localsearch.neighborhood.OutsourcingNeighborhood
import models.problem.binpacking.localsearch.neighborhood.Right
import models.problem.binpacking.localsearch.neighborhood.Up
import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.Rectangle

import scala.collection.View

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
  override val boxLength: Int
) extends BinPackingSolutionHandler with GeometricStartSolution with BoxWeightedTopLeftFirstEvaluation
    with OverlapPenalization {

  override val startSolution: BinPackingSolution = overconfidentStartSolution

  private val boxPullUpNeighborhood = new BoxPullUpNeighborhood(boxLength)
  private val geometricShiftNeighborhood = new GeometricShiftNeighborhood(boxLength)
  private val outsourcingNeighborhood = new OutsourcingNeighborhood(boxLength)

  override def getNeighborhood(solution: BinPackingSolution): View[BinPackingSolution] = {
    val solutionsWithBoxPullUp = boxPullUpNeighborhood.createBoxPullUpNeighborhood(solution)
    val solutionsWithLeftShift =
      geometricShiftNeighborhood.createShiftedSolutions(solution, Left, 1, allowOverlap = true)
    val solutionsWithRightShift =
      geometricShiftNeighborhood.createShiftedSolutions(solution, Right, 1, allowOverlap = true)
    val solutionsWithUpShift = geometricShiftNeighborhood.createShiftedSolutions(solution, Up, 1, allowOverlap = true)
    val solutionsWithDownShift =
      geometricShiftNeighborhood.createShiftedSolutions(solution, Down, 1, allowOverlap = true)
    val solutionsWithOutsourcedRectangle = outsourcingNeighborhood.createOutsourcingNeighborhood(solution)
    solutionsWithBoxPullUp ++
      solutionsWithLeftShift ++
      solutionsWithRightShift ++
      solutionsWithUpShift ++
      solutionsWithDownShift ++
      solutionsWithOutsourcedRectangle
  }

  def maxAllowedOverlap(step: Int): Double = {
    0
  }

  override def evaluate(solution: BinPackingSolution, step: Int): Score = {
    val overlapPenalization = penalizeOverlap(solution, maxAllowedOverlap(step))
    PrioritizedPenalizationScore(
      evaluate(solution),
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
