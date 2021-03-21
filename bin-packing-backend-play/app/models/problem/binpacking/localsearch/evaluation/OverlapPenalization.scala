package models.problem.binpacking.localsearch.evaluation

import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.utils.PairBuildingUtil

trait OverlapPenalization {

  def overlap(placingA: (Rectangle, Coordinates), placingB: (Rectangle, Coordinates)): Double = {
    val (rectangleA, coordinatesA) = placingA
    val (rectangleB, coordinatesB) = placingB
    val commonAreaTopLeft = Coordinates(
      Math.max(coordinatesA.x, coordinatesB.x),
      Math.max(coordinatesA.y, coordinatesB.y)
    )
    val commonAreaBottomRight = Coordinates(
      Math.min(coordinatesA.x + rectangleA.width, coordinatesB.x + rectangleB.width),
      Math.min(coordinatesA.y + rectangleA.height, coordinatesB.y + rectangleB.height)
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

  def escapeDistance(placingA: (Rectangle, Coordinates), placingB: (Rectangle, Coordinates)): Int = {
    val (rectangleA, coordinatesA) = placingA
    val (rectangleB, coordinatesB) = placingB
    val leftDistance = coordinatesB.x + rectangleB.width - coordinatesA.x
    val rightDistance = coordinatesA.x + rectangleA.width - coordinatesB.x
    val topDistance = coordinatesB.y + rectangleB.height - coordinatesA.y
    val bottomDistance = coordinatesA.y + rectangleA.height - coordinatesB.y
    Seq(leftDistance, rightDistance, topDistance, bottomDistance).min
  }

  def penalizeOverlap(solution: BinPackingSolution, maxAllowedOverlap: Double): Double = {
    val placingPairsExceedingAllowedOverlap = solution.getPlacementsPerBox.values.flatMap { placement =>
      val placingPairs = PairBuildingUtil.buildPairs(placement)
      placingPairs.filter { case (placingA, placingB) => overlap(placingA, placingB) > maxAllowedOverlap }
    }
    val escapeDistances = placingPairsExceedingAllowedOverlap.map {
      case (placingA, placingB) => escapeDistance(placingA, placingB)
    }
    escapeDistances.sum
  }

}


