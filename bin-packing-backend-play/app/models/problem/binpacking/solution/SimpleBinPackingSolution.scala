package models.problem.binpacking.solution

case class SimpleBinPackingSolution(
  override val placement: Map[Rectangle, Placing]
) extends BinPackingSolution {

  override def asSimpleSolution: SimpleBinPackingSolution = this

  override def updated(rectangle: Rectangle, placing: Placing): SimpleBinPackingSolution = {
    SimpleBinPackingSolution(
      placement.filterNot {
        case (oldRectangle, _) => oldRectangle.id == rectangle.id
      } + (rectangle -> placing)
    )
  }

  override def reset(placement: Map[Rectangle, Placing]): SimpleBinPackingSolution = {
    SimpleBinPackingSolution(placement)
  }

  override def reorderBoxes(boxIdOrder: Seq[Int]): SimpleBinPackingSolution = {
    val boxIdMapping = boxIdOrder.zip(1 to boxIdOrder.size).toMap
    SimpleBinPackingSolution(
      placement.map {
        case (rectangle, Placing(Box(id, length), coordinates)) =>
          rectangle -> Placing(Box(boxIdMapping(id), length), coordinates)
      }
    )
  }
}
