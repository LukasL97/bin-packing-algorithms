package models.problem.binpacking

import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.BinPackingSolutionRepresentation
import models.problem.binpacking.solution.SimpleBinPackingSolutionRepresentation

trait BinPacking {

  val instance: BinPackingInstance

  def startSolution: BinPackingSolution

  def transformToStoredRepresentation(solution: BinPackingSolution): BinPackingSolutionRepresentation = {
    val simpleSolution = solution.asSimpleSolution
    SimpleBinPackingSolutionRepresentation(
      simpleSolution.placement,
      simpleSolution.update
    )
  }

}
