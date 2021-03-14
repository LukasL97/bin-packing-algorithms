package models.problem.binpacking.localsearch.evaluation

import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle

import scala.math.BigDecimal.RoundingMode

trait BoxWeightedTopLeftFirstEvaluation {

  val boxLength: Int
  val minimalCostWeight: Double = 0.9

  def evaluate(solution: BinPackingSolution): BigDecimal = {
    val pointCostFunction = buildLinearPointCostFunction(minimalCostWeight, boxLength)
    val boxIds = solution.placement.map {
      case (_, Placing(box, _)) => box.id
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

}
