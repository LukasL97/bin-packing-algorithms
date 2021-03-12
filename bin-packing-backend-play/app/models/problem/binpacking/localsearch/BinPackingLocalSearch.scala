package models.problem.binpacking.localsearch

import models.algorithm.LocalSearch
import models.algorithm.SolutionHandler
import models.problem.binpacking.BinPacking
import models.problem.binpacking.BinPackingSolutionValidator
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.SimpleBinPackingSolution

import scala.util.Random

trait BinPackingLocalSearch extends BinPacking {
  val solutionHandler: BinPackingSolutionHandler
  lazy val localSearch = new LocalSearch[SimpleBinPackingSolution](solutionHandler)

  override def startSolution: SimpleBinPackingSolution = solutionHandler.startSolution
}

trait BinPackingSolutionHandler
    extends SolutionHandler[SimpleBinPackingSolution] with BinPackingSolutionValidator
