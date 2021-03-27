package models.problem.binpacking.solution

import models.problem.binpacking.solution.initialization.EmptySolutionInitializer
import models.problem.binpacking.solution.initialization.FullPlacementSolutionInitializer
import models.problem.binpacking.solution.initialization.OneRectanglePerBoxSolutionInitializer
import models.problem.binpacking.solution.transformation.BoxReorderingSupport
import models.problem.binpacking.solution.transformation.PlacementResetSupport
import models.problem.binpacking.solution.transformation.RectanglePlacingUpdateSupport
import models.problem.binpacking.solution.transformation.SquashingSupport

object SimpleBinPackingSolution
    extends FullPlacementSolutionInitializer[SimpleBinPackingSolution]
    with EmptySolutionInitializer[SimpleBinPackingSolution]
    with OneRectanglePerBoxSolutionInitializer[SimpleBinPackingSolution] {

  def apply(boxLength: Int): SimpleBinPackingSolution = new SimpleBinPackingSolution(Map.empty[Rectangle, Placing])

  def apply(placement: Map[Rectangle, Placing]): SimpleBinPackingSolution = {
    new SimpleBinPackingSolution(placement)
  }

  def apply(rectangles: Seq[Rectangle], boxLength: Int): SimpleBinPackingSolution = new SimpleBinPackingSolution(
    rectangles.zipWithIndex.map {
      case (rectangle, index) => rectangle -> Placing(Box(index + 1, boxLength), Coordinates(0, 0))
    }.toMap
  )
}

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
    SimpleBinPackingSolution(
      reorderPlacement(boxIdOrder)
    )
  }

  override def squashed: SimpleBinPackingSolution = {
    val boxIdSquashMapping = getBoxIdSquashMapping
    reset(
      squashPlacement(boxIdSquashMapping)
    )
  }
}
