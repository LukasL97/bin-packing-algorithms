package models.problem.binpacking.localsearch

import models.problem.binpacking.BinPackingSolution
import models.problem.binpacking.Box
import models.problem.binpacking.Coordinates
import models.problem.binpacking.Placing
import models.problem.binpacking.Rectangle
import play.api.Logging

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
  rectangles: Set[Rectangle],
  boxLength: Int,
) extends BinPackingSolutionHandler with Logging {

  override val startSolution: BinPackingSolution = {
    val solution = BinPackingSolution(
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

  private def shiftUpSolution(solution: BinPackingSolution): BinPackingSolution = {
    val usedBoxIds = solution.placement.map {
      case (rectangle, Placing(box, coordinates)) => box.id
    }.toSet
    val skippedBoxIds = (1 to usedBoxIds.max).filter(!usedBoxIds.contains(_))
    skippedBoxIds match {
      case Seq() => solution
      case Seq(skippedBoxId) => BinPackingSolution(
        solution.placement.map {
          case (rectangle, Placing(box, coordinates)) if box.id <= skippedBoxId =>
            rectangle -> Placing(box, coordinates)
          case (rectangle, Placing(box, coordinates)) if box.id > skippedBoxId =>
            rectangle -> Placing(box.copy(id = box.id - 1), coordinates)
        }
      )
      case ids if ids.size > 1 => throw new RuntimeException("More than 1 box emptied in one neighborhood step")
    }
  }

  override def getNeighborhood(solution: BinPackingSolution): Set[BinPackingSolution] = {
    val solutionsWithBoxPullUp = solution.placement.collect {
      case (rectangle, Placing(Box(id, length), coordinates)) if id > 1 =>
        BinPackingSolution(
          solution.placement.updated(
            rectangle,
            Placing(Box(id - 1, length), Coordinates(length - rectangle.width, length - rectangle.height))
          )
        )
    }.map(shiftUpSolution).toSet
    val solutionsWithUpShift = solution.placement.map {
      case (rectangle, Placing(box, Coordinates(x, y))) =>
        BinPackingSolution(
          solution.placement.updated(
            rectangle,
            Placing(box, Coordinates(x, y - 1))
          )
        )
    }.toSet
    val solutionsWithLeftShift = solution.placement.map {
      case (rectangle, Placing(box, Coordinates(x, y))) =>
        BinPackingSolution(
          solution.placement.updated(
            rectangle,
            Placing(box, Coordinates(x - 1, y))
          )
        )
    }.toSet
    val neighborhood = (solutionsWithBoxPullUp ++ solutionsWithUpShift ++ solutionsWithLeftShift).filter(isFeasible)
    neighborhood
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

  override def evaluate(solution: BinPackingSolution): BigDecimal = {
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
