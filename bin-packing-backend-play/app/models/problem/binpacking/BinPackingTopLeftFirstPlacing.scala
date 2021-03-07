package models.problem.binpacking

trait BinPackingTopLeftFirstPlacing extends BinPackingSolutionValidator {

  val boxLength: Int

  def placeRectangleInBoxAtMostTopLeftPoint(
    rectangle: Rectangle,
    placement: Map[Rectangle, Coordinates]
  ): Option[Coordinates] = {
    coordinatesInTopLeftFirstOrder.find { coordinates =>
      val newPlacement = placement + (rectangle -> coordinates)
      allRectanglesInBoundsForSingleBox(newPlacement, boxLength) && allRectanglesDisjunctiveInSingleBox(newPlacement)
    }
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
