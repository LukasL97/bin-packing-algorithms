package models.problem.binpacking


trait BinPackingSolution {
  val placement: Map[Rectangle, Placing]

  def updated(rectangle: Rectangle, placing: Placing): BinPackingSolution

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

case class SimpleBinPackingSolution(
  override val placement: Map[Rectangle, Placing]
) extends BinPackingSolution {

  override def updated(rectangle: Rectangle, placing: Placing): SimpleBinPackingSolution = {
    SimpleBinPackingSolution(
      placement.filterNot {
        case (oldRectangle, _) => oldRectangle.id == rectangle.id
      } + (rectangle -> placing)
    )
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
