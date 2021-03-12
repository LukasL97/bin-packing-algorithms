package models.problem.binpacking

import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Rectangle

trait BinPackingTopLeftFirstPlacing extends BinPackingSolutionValidator {

  val boxLength: Int

  def placeRectangleInBoxAtMostTopLeftPoint(
    rectangle: Rectangle,
    placement: Map[Rectangle, Coordinates],
    considerRotation: Boolean
  ): Option[(Rectangle, Coordinates)] = {
    val rotatedRectangle = rectangle.rotated
    coordinatesInTopLeftFirstOrder.collectFirst {
      case coordinates if isPlaceable(rectangle, coordinates, placement) => rectangle -> coordinates
      case coordinates if considerRotation && isPlaceable(rotatedRectangle, coordinates, placement) =>
        rotatedRectangle -> coordinates
    }
  }

  private def isPlaceable(
    rectangle: Rectangle,
    coordinates: Coordinates,
    placement: Map[Rectangle, Coordinates]
  ): Boolean = {
    val newPlacement = placement + (rectangle -> coordinates)
    allRectanglesInBoundsForSingleBox(newPlacement, boxLength) && allRectanglesDisjunctiveInSingleBox(newPlacement)
  }

  private implicit val topLeftFirstOrdering: Ordering[Coordinates] = (left: Coordinates, right: Coordinates) => {
    (left.x + left.y) - (right.x + right.y) match {
      case 0 => left.x - right.x
      case other => other
    }
  }

  private lazy val coordinatesInTopLeftFirstOrder = (0 until boxLength)
    .flatMap(x => (0 until boxLength).map(y => Coordinates(x, y)))
    .sorted

}
