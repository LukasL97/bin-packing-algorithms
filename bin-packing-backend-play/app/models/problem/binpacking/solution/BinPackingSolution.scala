package models.problem.binpacking.solution

trait BinPackingSolution {
  val placement: Map[Rectangle, Placing]

  def updated(rectangle: Rectangle, placing: Placing): BinPackingSolution

  def updated(placement: Map[Rectangle, Placing]): BinPackingSolution = {
    placement.foldLeft(this) {
      case (updatedSolution, (rectangle, placing)) => updatedSolution.updated(rectangle, placing)
    }
  }

  def reset(placement: Map[Rectangle, Placing]): BinPackingSolution

  def squash: BinPackingSolution = {
    val boxIds = placement.values.map(_.box.id).toSeq.distinct.sorted
    val boxIdSquashMapping = boxIds.zip(1 to boxIds.size).toMap
    reset(
      placement.map {
        case (rectangle, Placing(Box(id, length), coordinates)) =>
          rectangle -> Placing(Box(boxIdSquashMapping(id), length), coordinates)
      }
    )
  }

  def getPlacementsPerBox: Map[Int, Map[Rectangle, Coordinates]] = {
    placement.groupBy {
      case (_, placing) => placing.box
    }.toSeq.sortBy {
      case (box, _) => box.id
    }.map {
      case (box, placement) => box.id -> placement.map { case (rectangle, placing) => rectangle -> placing.coordinates }
    }.toMap
  }

  def getPlacementInSingleBox(boxId: Int): Map[Rectangle, Coordinates] = {
    getPlacementsPerBox(boxId)
  }
}

case class Rectangle(
  id: Int,
  width: Int,
  height: Int
) {
  def rotated: Rectangle = Rectangle(id = id, width = height, height = width)
}

case class Placing(
  box: Box,
  coordinates: Coordinates
)

case class Box(
  id: Int,
  length: Int
)

case class Coordinates(
  x: Int,
  y: Int
)
