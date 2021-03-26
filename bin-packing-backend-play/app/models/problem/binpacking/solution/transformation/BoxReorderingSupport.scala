package models.problem.binpacking.solution.transformation

import models.problem.binpacking.solution.BinPackingSolution

trait BoxReorderingSupport[A <: BoxReorderingSupport[A]] extends BinPackingSolution {
  def reorderBoxes(boxIdOrder: Seq[Int]): A
}
