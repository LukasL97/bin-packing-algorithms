package models.problem.binpacking.localsearch
import models.problem.binpacking.localsearch.evaluation.BoxWeightedTopLeftFirstEvaluation
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
import models.problem.binpacking.utils.PairBuildingUtil

import java.math.MathContext

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
) extends BinPackingSolutionHandler with GeometricShiftNeighborhood with BoxWeightedTopLeftFirstEvaluation {

  override val startSolution: BinPackingSolution = SimpleBinPackingSolution(
    rectangles
      .map(rectangle => rectangle -> Placing(Box(1, boxLength), Coordinates(0, 0)))
      .toMap
  )

  override def getNeighborhood(solution: BinPackingSolution): Set[BinPackingSolution] = {
    val solutionsWithLeftShift = createShiftedSolutions(solution, Left, 1, ensureFeasibility = false)
    val solutionsWithRightShift = createShiftedSolutions(solution, Right, 1, ensureFeasibility = false)
    val solutionsWithUpShift = createShiftedSolutions(solution, Up, 1, ensureFeasibility = false)
    val solutionsWithDownShift = createShiftedSolutions(solution, Down, 1, ensureFeasibility = false)
    solutionsWithLeftShift ++ solutionsWithRightShift ++ solutionsWithUpShift ++ solutionsWithDownShift
  }

  private val infinity = BigDecimal(10).pow(1000)

  private def maxAllowedOverlap(step: Int): Double = {
    1 - step * 0.01
  }

  private def overlap(placingA: (Rectangle, Coordinates), placingB: (Rectangle, Coordinates)): Double = {
    val (rectangleA, coordinatesA) = placingA
    val (rectangleB, coordinatesB) = placingB
    val commonAreaTopLeft = Coordinates(
      Math.max(coordinatesA.x, coordinatesB.x),
      Math.max(coordinatesA.y, coordinatesB.y)
    )
    val commonAreaBottomRight = Coordinates(
      Math.max(coordinatesA.x + rectangleA.width, coordinatesB.x + rectangleB.width),
      Math.max(coordinatesA.y + rectangleA.height, coordinatesB.y + rectangleB.height)
    )
    val commonAreaWidth = commonAreaBottomRight.x - commonAreaTopLeft.x
    val commonAreaHeight = commonAreaBottomRight.y - commonAreaTopLeft.y
    if (commonAreaWidth > 0 && commonAreaHeight > 0) {
      (commonAreaWidth * commonAreaHeight).toDouble / Math.max(
        rectangleA.width * rectangleA.height,
        rectangleB.width * rectangleB.height
      )
    } else {
      0.0
    }
  }

  override def evaluate(solution: BinPackingSolution, step: Int): BigDecimal = {
    val maxPairwiseOverlap = solution.getPlacementsPerBox.values
      .flatMap { placement =>
        val placingPairs = PairBuildingUtil.buildPairs(placement)
        placingPairs.map { case (placingA, placingB) => overlap(placingA, placingB)}
      }
      .max
    if (maxPairwiseOverlap > maxAllowedOverlap(step)) {
      infinity
    } else {
      val score = evaluate(solution)
      assert(score > infinity)
      score
    }
  }
}
