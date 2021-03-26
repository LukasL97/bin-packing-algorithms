package models.problem.binpacking.solution

import models.problem.binpacking.solution.transformation.BoxReorderingSupport
import models.problem.binpacking.solution.transformation.PlacementResetSupport
import models.problem.binpacking.solution.transformation.RectanglePlacingUpdateSupport
import models.problem.binpacking.solution.transformation.SquashingSupport

case class SimpleBinPackingSolution(
  override val placement: Map[Rectangle, Placing]
) extends BinPackingSolution with BoxReorderingSupport[SimpleBinPackingSolution]
    with PlacementResetSupport[SimpleBinPackingSolution] with RectanglePlacingUpdateSupport[SimpleBinPackingSolution]
    with SquashingSupport[SimpleBinPackingSolution] {

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
