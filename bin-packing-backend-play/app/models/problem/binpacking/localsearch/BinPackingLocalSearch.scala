package models.problem.binpacking.localsearch

import models.algorithm.LocalSearch
import models.algorithm.SolutionHandler
import models.problem.binpacking.BinPacking
import models.problem.binpacking.BinPackingSolutionValidator
import models.problem.binpacking.solution.BinPackingSolution

trait BinPackingLocalSearch[A <: BinPackingSolution] extends BinPacking {
  val solutionHandler: BinPackingSolutionHandler[A]
  lazy val localSearch = new LocalSearch[A](solutionHandler)

  override def startSolution: BinPackingSolution = solutionHandler.startSolution
}

trait BinPackingSolutionHandler[A <: BinPackingSolution]
    extends SolutionHandler[A] with BinPackingSolutionValidator
