package models.problem.binpacking

import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.utils.TopLeftFirstCoordinateOrdering

trait BinPackingTopLeftFirstPlacing extends BinPackingSolutionValidator with TopLeftFirstCoordinateOrdering {

  val boxLength: Int

  lazy val horizontalStepSize: Int = 1
  lazy val verticalStepSize: Int = 1

  def placeRectangleInBoxAtMostTopLeftPoint(
    rectangle: Rectangle,
    placement: Map[Rectangle, Coordinates],
    considerRotation: Boolean,
    candidateCoordinates: Option[Seq[Coordinates]] = None
  ): Option[(Rectangle, Coordinates)] = {
    val candidates = candidateCoordinates.getOrElse(coordinatesInTopLeftFirstOrder)
    val rotatedRectangle = rectangle.rotated
    candidates.collectFirst {
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
    validateNewPlacingInSingleBox(rectangle, coordinates, placement, boxLength)
  }

  private lazy val coordinatesInTopLeftFirstOrder = {
    val coordinates = for {
      x <- (0 until boxLength).by(horizontalStepSize)
      y <- (0 until boxLength).by(verticalStepSize)
    } yield Coordinates(x, y)
    coordinates.sorted
  }

}
