package models.problem.binpacking.solution.initialization

import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.Rectangle

trait OneRectanglePerBoxSolutionInitializer[A <: BinPackingSolution] {
  def apply(rectangles: Seq[Rectangle], boxLength: Int): A
}
