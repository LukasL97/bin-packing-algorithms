package models.problem.binpacking.solution.transformation

import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle

trait SquashingSupport[A <: SquashingSupport[A]] extends BinPackingSolution {
  def squashed: A

  protected def getBoxIdSquashMapping: Map[Int, Int] = {
    val boxIds = placement.values.map(_.box.id).toSeq.distinct.sorted
    boxIds.zip(1 to boxIds.size).toMap
  }

  protected def squashPlacement(boxIdSquashMapping: Map[Int, Int]): Map[Rectangle, Placing] = {
    placement.map {
      case (rectangle, Placing(Box(id, length), coordinates)) =>
        rectangle -> Placing(Box(boxIdSquashMapping(id), length), coordinates)
    }
  }
}
