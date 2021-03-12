package models.problem.binpacking


case class BinPackingSolution(
  placement: Map[Rectangle, Placing]
) {

  def updated(rectangle: Rectangle, placing: Placing): BinPackingSolution = {
    BinPackingSolution(
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
