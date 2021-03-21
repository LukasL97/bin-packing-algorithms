package models.problem.binpacking.localsearch.evaluation

import metrics.Metrics
import models.algorithm.OneDimensionalScore
import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle

trait BoxWeightedTopLeftFirstEvaluation extends Metrics {

  val boxLength: Int
  val minimalCostWeight: Double = 0.9

  def evaluate(solution: BinPackingSolution): BoxWeightedScore = {
    val pointCostFunction = buildLinearPointCostFunction(minimalCostWeight, boxLength)
    val boxIds = solution.placement.map {
      case (_, Placing(box, _)) => box.id
    }.toSeq.distinct.sorted
    require(boxIds == (1 to boxIds.max), "Solution has empty intermediate boxes")
    val sortedPlacementPerBox = solution.getPlacementsPerBox.toSeq.sortBy {
      case (boxId, _) => boxId
    }
    val boxSkewedFillRates = sortedPlacementPerBox.map {
      case (_, placement) =>
        placement.map {
          case (rectangle, coordinates) => calculateRectangleCost(pointCostFunction, rectangle, coordinates)
        }.sum
    }
    BoxWeightedScore(
      boxSkewedFillRates.map(OneDimensionalScore)
    )
  }

  private[evaluation] def buildLinearPointCostFunction(minimalCostWeight: Double, boxLength: Int): Int => Double = {
    require(minimalCostWeight < 1.0 && minimalCostWeight > 0.0)
    val slope = 2 * (1 - minimalCostWeight) / (2 * boxLength - 2)
    coordinateSum =>
      (slope * coordinateSum + minimalCostWeight) / (boxLength * boxLength)
  }

  private[evaluation] def calculateRectangleCost(
    pointCostFunction: Int => Double,
    rectangle: Rectangle,
    coordinates: Coordinates
  ): Double = withTimer("calculate-rectangle-cost") {
    val minEdgeLength = Math.min(rectangle.width, rectangle.height)
    val maxEdgeLength = Math.max(rectangle.width, rectangle.height)
    val bottomRight = Coordinates(coordinates.x + rectangle.width - 1, coordinates.y + rectangle.width - 1)
    val topLeftAndBottomRightCornerSummedPointCosts = (0 until (minEdgeLength - 1)).map { offset =>
      val topLeftDiagonalCosts = (offset + 1) * pointCostFunction(coordinates.x + coordinates.y + offset)
      val bottomRightDiagonalCosts = (offset + 1) * pointCostFunction(bottomRight.x + bottomRight.y - offset)
      topLeftDiagonalCosts + bottomRightDiagonalCosts
    }.sum
    val centralDiagonalsSummedPointCosts =
      (minEdgeLength - 1)
        .until(maxEdgeLength)
        .map(offset => minEdgeLength * pointCostFunction(offset + coordinates.x + coordinates.y))
        .sum
    topLeftAndBottomRightCornerSummedPointCosts + centralDiagonalsSummedPointCosts
  }

}
