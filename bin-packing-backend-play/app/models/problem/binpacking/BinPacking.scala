package models.problem.binpacking

import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.Rectangle

import scala.util.Random

trait BinPacking {

  val instance: BinPackingInstance

  def startSolution: BinPackingSolution

}
