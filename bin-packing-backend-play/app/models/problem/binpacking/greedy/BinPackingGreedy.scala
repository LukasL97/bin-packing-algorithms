package models.problem.binpacking.greedy

import models.algorithm.Greedy
import models.algorithm.SelectionHandler
import models.problem.binpacking.BinPacking
import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.Rectangle

trait BinPackingGreedy[A <: BinPackingSolution] extends BinPacking {
  val selectionHandler: BinPackingSelectionHandler[A]
  lazy val greedy = new Greedy[Rectangle, A](selectionHandler)

  override def startSolution: A = selectionHandler.startSolution
}

trait BinPackingSelectionHandler[A <: BinPackingSolution] extends SelectionHandler[Rectangle, A]
