package models.problem.binpacking.localsearch

import models.problem.binpacking.SimpleBinPackingSolution
import models.problem.binpacking.Box
import models.problem.binpacking.Coordinates
import models.problem.binpacking.Placing
import models.problem.binpacking.Rectangle
import models.problem.binpacking.localsearch.neighborhood.BoxPullUpNeighborhood
import models.problem.binpacking.localsearch.neighborhood.GeometricShiftNeighborhood
import models.problem.binpacking.localsearch.neighborhood.Up
import models.problem.binpacking.localsearch.neighborhood.Left

import scala.math.BigDecimal.RoundingMode

class GeometryBasedBinPacking(
  override val boxLength: Int,
  override val numRectangles: Int,
  override val rectangleWidthRange: (Int, Int),
  override val rectangleHeightRange: (Int, Int)
) extends BinPackingLocalSearch {

  override val solutionHandler: BinPackingSolutionHandler =
    new GeometryBasedBinPackingSolutionHandler(rectangles, boxLength)

}

class GeometryBasedBinPackingSolutionHandler(
  val rectangles: Set[Rectangle],
  val boxLength: Int,
) extends BinPackingSolutionHandler with BoxPullUpNeighborhood with GeometricShiftNeighborhood {

  override val startSolution: SimpleBinPackingSolution = {
    val solution = SimpleBinPackingSolution(
      rectangles
        .map(rectangle => rectangle -> Placing(
          Box(rectangle.id, boxLength),
          Coordinates(0, 0)
        ))
        .toMap
    )
    if (isFeasible(solution)) {
      solution
    } else {
      throw new RuntimeException("Created infeasible solution as starting solution")
    }
  }

  override def getNeighborhood(solution: SimpleBinPackingSolution): Set[SimpleBinPackingSolution] = {
    val solutionsWithBoxPullUp = createBoxPullUpNeighborhood(solution)
    val solutionsWithUpShift = createMaximallyShiftedSolutions(solution, Up)
    val solutionsWithLeftShift = createMaximallyShiftedSolutions(solution, Left)
    solutionsWithBoxPullUp ++ solutionsWithUpShift ++ solutionsWithLeftShift
  }

  def buildLinearPointCostFunction(minimalCostWeight: BigDecimal, boxLength: Int): Coordinates => BigDecimal = {
    require(minimalCostWeight < 1.0 && minimalCostWeight > 0.0)
    val slope = 2 * (1 - minimalCostWeight) / (2 * boxLength - 2)
    coordinates => (slope * (coordinates.x + coordinates.y) + minimalCostWeight) / (boxLength * boxLength)
  }

  def calculateRectangleCost(
    pointCostFunction: Coordinates => BigDecimal,
    rectangle: Rectangle,
    coordinates: Coordinates
  ): BigDecimal = {
    (coordinates.x until (coordinates.x + rectangle.width))
      .flatMap(x => (coordinates.y until (coordinates.y + rectangle.height)).map(y => Coordinates(x, y)))
      .map(pointCostFunction)
      .sum
  }

  private def calculateBoxCostFactor(pointCostFunction: Coordinates => BigDecimal, id: Int): BigDecimal = {
    val minBoxCostChange = pointCostFunction(Coordinates(1, 0)) - pointCostFunction(Coordinates(0, 0))
    require(minBoxCostChange > 0)
    (1 / minBoxCostChange).setScale(0, RoundingMode.UP).pow(id)
  }

  override def evaluate(solution: SimpleBinPackingSolution): BigDecimal = {
    val minimalCostWeight = 0.9
    val pointCostFunction = buildLinearPointCostFunction(minimalCostWeight, boxLength)
    val boxIds = solution.placement.map {
      case (rectangle, Placing(box, coordinates)) => box.id
    }.toSeq.sorted
    val boxSkewedFillRates = boxIds.map { id =>
      val rectanglesInBox = solution.placement.collect {
        case (rectangle, Placing(box, coordinates)) if box.id == id => rectangle -> coordinates
      }
      rectanglesInBox.map {
        case (rectangle, coordinates) => calculateRectangleCost(pointCostFunction, rectangle, coordinates)
      }.sum
    }
    val boxCostFactors = boxIds.map(id => calculateBoxCostFactor(pointCostFunction, id))
    (boxCostFactors zip boxSkewedFillRates)
      .map {
        case (factor, rate) => factor * rate
      }.sum
  }

}
