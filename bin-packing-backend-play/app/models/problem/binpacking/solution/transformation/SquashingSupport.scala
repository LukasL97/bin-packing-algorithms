package models.problem.binpacking.solution.transformation

import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Placing

trait SquashingSupport[A <: SquashingSupport[A]] extends BinPackingSolution with PlacementResetSupport[A] {
  def squashed: A = {
    val boxIds = placement.values.map(_.box.id).toSeq.distinct.sorted
    val boxIdSquashMapping = boxIds.zip(1 to boxIds.size).toMap
    reset(
      placement.map {
        case (rectangle, Placing(Box(id, length), coordinates)) =>
          rectangle -> Placing(Box(boxIdSquashMapping(id), length), coordinates)
      }
    )
  }
}
