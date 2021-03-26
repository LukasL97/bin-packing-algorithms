package models.problem.binpacking.solution.initialization

import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle

trait FullPlacementSolutionInitializer[A <: BinPackingSolution] {
  def apply(placement: Map[Rectangle, Placing]): A
}
