package models.problem.rectangles.localsearch

import models.algorithm.LocalSearch
import models.algorithm.SolutionHandler
import models.problem.rectangles.Coordinates
import models.problem.rectangles.Placing
import models.problem.rectangles.Rectangle
import models.problem.rectangles.RectanglesPlacement
import models.problem.rectangles.RectanglesPlacementSolution

import scala.util.Random

trait RectanglesPlacementLocalSearch extends RectanglesPlacement {
  val solutionHandler: RectanglesPlacementSolutionHandler
  lazy val localSearch = new LocalSearch[RectanglesPlacementSolution](solutionHandler)
}

trait RectanglesPlacementSolutionHandler extends SolutionHandler[RectanglesPlacementSolution] {

  def isFeasible(solution: RectanglesPlacementSolution): Boolean = {
    allRectanglesInBox(solution) && allRectanglesDisjunctive(solution)
  }

  def allRectanglesDisjunctive(solution: RectanglesPlacementSolution): Boolean = {
    solution.placement.groupBy {
      case (rectangle, placing) => placing.box
    }.map {
      case (box, placement) =>
        isFeasibleInSingleBox(placement.map {
          case (rectangle, placing) => (rectangle, placing.coordinates)
        })
    }.forall(identity)
  }

  def isFeasibleInSingleBox(placement: Map[Rectangle, Coordinates]): Boolean =
    buildPairs(placement).map {
      case (placingA, placingB) => disjunctive(placingA, placingB)
    }.forall(identity)

  def buildPairs(placement: Map[Rectangle, Coordinates]): Seq[((Rectangle, Coordinates), (Rectangle, Coordinates))] = {
    val placementSeq = placement.toSeq
    placementSeq.zipWithIndex.flatMap {
      case ((rectangleA, coordinatesA), index) =>
        placementSeq.take(index).map {
          case (rectangleB, coordinatesB) => ((rectangleA, coordinatesA), (rectangleB, coordinatesB))
        }
    }
  }

  def disjunctive(placingA: (Rectangle, Coordinates), placingB: (Rectangle, Coordinates)): Boolean = {
    val (rectangleA, coordinatesA) = placingA
    val (rectangleB, coordinatesB) = placingB
    val aLeftOfB = coordinatesA.x + rectangleA.width <= coordinatesB.x
    val aRightOfB = coordinatesA.x >= coordinatesB.x + rectangleB.width
    val aAboveB = coordinatesA.y + rectangleA.height <= coordinatesB.y
    val aBelowB = coordinatesA.y >= coordinatesB.y + rectangleB.height
    aLeftOfB || aRightOfB || aAboveB || aBelowB
  }

  def allRectanglesInBox(solution: RectanglesPlacementSolution): Boolean =
    solution.placement.map {
      case (rectangle, Placing(box, Coordinates(x, y))) =>
        0 <= x && x + rectangle.width <= box.width && 0 <= y && y + rectangle.height <= box.height
    }.forall(identity)

}
