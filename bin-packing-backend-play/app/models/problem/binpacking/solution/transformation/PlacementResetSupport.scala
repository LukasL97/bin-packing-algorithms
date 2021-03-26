package models.problem.binpacking.solution.transformation

import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle

trait PlacementResetSupport[A <: PlacementResetSupport[A]] extends BinPackingSolution {
  def reset(placement: Map[Rectangle, Placing]): A
}
