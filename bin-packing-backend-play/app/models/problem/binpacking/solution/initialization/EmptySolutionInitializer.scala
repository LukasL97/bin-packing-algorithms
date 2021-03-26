package models.problem.binpacking.solution.initialization

import models.problem.binpacking.solution.BinPackingSolution

trait EmptySolutionInitializer[A <: BinPackingSolution] {
  def apply(boxLength: Int): A
}
