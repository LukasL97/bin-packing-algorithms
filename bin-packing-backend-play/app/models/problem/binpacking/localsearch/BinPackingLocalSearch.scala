package models.problem.binpacking.localsearch

import models.algorithm.LocalSearch
import models.algorithm.SolutionHandler
import models.problem.binpacking.BinPacking
import models.problem.binpacking.BinPackingSolutionValidator
import models.problem.binpacking.solution.BinPackingSolution

trait BinPackingLocalSearch extends BinPacking {
  val solutionHandler: BinPackingSolutionHandler
  lazy val localSearch = new LocalSearch[BinPackingSolution](solutionHandler)

  override def startSolution: BinPackingSolution = solutionHandler.startSolution
}

trait BinPackingSolutionHandler
    extends SolutionHandler[BinPackingSolution] with BinPackingSolutionValidator
