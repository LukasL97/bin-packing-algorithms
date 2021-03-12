package models.problem.binpacking.localsearch

import models.algorithm.LocalSearch
import models.algorithm.SolutionHandler
import models.problem.binpacking.Coordinates
import models.problem.binpacking.Placing
import models.problem.binpacking.Rectangle
import models.problem.binpacking.BinPacking
import models.problem.binpacking.SimpleBinPackingSolution
import models.problem.binpacking.BinPackingSolutionValidator

import scala.util.Random

trait BinPackingLocalSearch extends BinPacking {
  val solutionHandler: BinPackingSolutionHandler
  lazy val localSearch = new LocalSearch[SimpleBinPackingSolution](solutionHandler)

  override def startSolution: SimpleBinPackingSolution = solutionHandler.startSolution
}

trait BinPackingSolutionHandler
    extends SolutionHandler[SimpleBinPackingSolution] with BinPackingSolutionValidator
