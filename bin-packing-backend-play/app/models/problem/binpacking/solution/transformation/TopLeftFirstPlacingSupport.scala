package models.problem.binpacking.solution.transformation

import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.Rectangle

trait TopLeftFirstPlacingSupport[A <: TopLeftFirstPlacingSupport[A]] extends BinPackingSolution {
  def placeTopLeftFirst(rectangle: Rectangle): A
}
