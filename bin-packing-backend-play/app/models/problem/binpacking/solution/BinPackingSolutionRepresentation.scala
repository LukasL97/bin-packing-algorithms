package models.problem.binpacking.solution

import models.problem.binpacking.solution.update.Update

trait BinPackingSolutionRepresentation {
  val placement: Map[Rectangle, Placing]
  val update: Update

  def setUpdate(update: Update): BinPackingSolutionRepresentation
}

case class SimpleBinPackingSolutionRepresentation(
  override val placement: Map[Rectangle, Placing],
  override val update: Update
) extends BinPackingSolutionRepresentation {
  override def setUpdate(update: Update): SimpleBinPackingSolutionRepresentation = copy(update = update)
}

case class RectanglePermutationBinPackingSolutionRepresentation(
  override val placement: Map[Rectangle, Placing],
  override val update: Update,
  permutation: Seq[Int]
) extends BinPackingSolutionRepresentation {
  override def setUpdate(update: Update): RectanglePermutationBinPackingSolutionRepresentation = copy(update = update)
}
