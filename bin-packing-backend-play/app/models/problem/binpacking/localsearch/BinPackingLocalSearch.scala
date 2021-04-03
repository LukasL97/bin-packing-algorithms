package models.problem.binpacking.localsearch

import models.algorithm.LocalSearch
import models.algorithm.SolutionHandler
import models.problem.binpacking.BinPacking
import models.problem.binpacking.BinPackingSolutionValidator
import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.update.UnchangedSolution
import models.problem.binpacking.solution.update.UpdateStoringSupport

trait BinPackingLocalSearch[A <: BinPackingSolution with UpdateStoringSupport[A]] extends BinPacking {
  val solutionHandler: BinPackingSolutionHandler[A]
  lazy val localSearch = new LocalSearch[A](solutionHandler)

  override def startSolution: BinPackingSolution = solutionHandler.startSolution
}

trait BinPackingSolutionHandler[A <: BinPackingSolution with UpdateStoringSupport[A]]
    extends SolutionHandler[A] with BinPackingSolutionValidator {

  override def tagAsUnchanged(solution: A): A = solution.setUpdate(UnchangedSolution())
}
