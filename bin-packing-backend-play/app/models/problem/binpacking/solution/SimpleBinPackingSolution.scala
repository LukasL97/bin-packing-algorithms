package models.problem.binpacking.solution

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

  override def updated(placement: Map[Rectangle, Placing]): SimpleBinPackingSolution = {
    SimpleBinPackingSolution(placement)
  }
}
