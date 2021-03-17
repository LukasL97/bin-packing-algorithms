package models.problem.binpacking

import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle

trait BinPackingSolutionValidator {

  def isFeasible(solution: BinPackingSolution): Boolean = {
    allRectanglesInBox(solution) && allRectanglesDisjunctive(solution)
  }

  def allRectanglesInBox(solution: BinPackingSolution): Boolean =
    solution.placement.map {
      case (rectangle, Placing(box, coordinates)) => inBounds(rectangle, coordinates, box.length)
    }.forall(identity)

  private def allRectanglesDisjunctive(solution: BinPackingSolution): Boolean = {
    solution.placement.groupBy {
      case (rectangle, placing) => placing.box
    }.map {
      case (box, placement) =>
        allRectanglesDisjunctiveInSingleBox(placement.map {
          case (rectangle, placing) => (rectangle, placing.coordinates)
        })
    }.forall(identity)
  }

  def allRectanglesDisjunctiveInSingleBox(placement: Map[Rectangle, Coordinates]): Boolean =
    buildPairs(placement).map {
      case (placingA, placingB) => disjunctive(placingA, placingB)
    }.forall(identity)

  /**
    * @param existingPlacement assumed to be a feasible placement
    */
  def validateNewPlacingInSingleBox(
    newRectangle: Rectangle,
    newCoordinates: Coordinates,
    existingPlacement: Map[Rectangle, Coordinates],
    boxLength: Int
  ): Boolean = {
    val newRectangleInBounds = inBounds(newRectangle, newCoordinates, boxLength)
    val newRectangleDisjunctiveToExistingRectangles = existingPlacement
      .forall(placing => disjunctive(placing, (newRectangle, newCoordinates)))
    newRectangleInBounds && newRectangleDisjunctiveToExistingRectangles
  }

  private def buildPairs(
    placement: Map[Rectangle, Coordinates]
  ): Seq[((Rectangle, Coordinates), (Rectangle, Coordinates))] = {
    val placementSeq = placement.toSeq
    placementSeq.zipWithIndex.flatMap {
      case ((rectangleA, coordinatesA), index) =>
        placementSeq.take(index).map {
          case (rectangleB, coordinatesB) => ((rectangleA, coordinatesA), (rectangleB, coordinatesB))
        }
    }
  }

  private def disjunctive(placingA: (Rectangle, Coordinates), placingB: (Rectangle, Coordinates)): Boolean = {
    val (rectangleA, coordinatesA) = placingA
    val (rectangleB, coordinatesB) = placingB
    val aLeftOfB = coordinatesA.x + rectangleA.width <= coordinatesB.x
    val aRightOfB = coordinatesA.x >= coordinatesB.x + rectangleB.width
    val aAboveB = coordinatesA.y + rectangleA.height <= coordinatesB.y
    val aBelowB = coordinatesA.y >= coordinatesB.y + rectangleB.height
    aLeftOfB || aRightOfB || aAboveB || aBelowB
  }

  def inBounds(rectangle: Rectangle, coordinates: Coordinates, boxLength: Int): Boolean = {
    val inBoundsLeft = 0 <= coordinates.x
    val inBoundsRight = coordinates.x + rectangle.width <= boxLength
    val inBoundsTop = 0 <= coordinates.y
    val inBoundsBottom = coordinates.y + rectangle.height <= boxLength
    inBoundsLeft && inBoundsRight && inBoundsTop && inBoundsBottom
  }
}
