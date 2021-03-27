package models.problem.binpacking.solution.transformation

import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle

trait BoxReorderingSupport[A <: BoxReorderingSupport[A]] extends BinPackingSolution {

  def reorderBoxes(boxIdOrder: Seq[Int]): A

  protected def reorderPlacement(boxIdOrder: Seq[Int]): Map[Rectangle, Placing] = {
    val boxIdMapping = boxIdOrder.zip(1 to boxIdOrder.size).toMap
    placement.map {
      case (rectangle, Placing(Box(id, length), coordinates)) =>
        rectangle -> Placing(Box(boxIdMapping(id), length), coordinates)
    }
  }
}
