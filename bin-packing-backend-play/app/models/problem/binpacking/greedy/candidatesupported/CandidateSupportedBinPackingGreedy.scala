package models.problem.binpacking.greedy.candidatesupported

import models.problem.binpacking.greedy.BinPackingGreedy
import models.problem.binpacking.greedy.BinPackingSelectionHandler
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.TopLeftFirstBinPackingSolution

trait CandidateSupportedBinPackingGreedy extends BinPackingGreedy[TopLeftFirstBinPackingSolution] {
  val selectionHandler: CandidateSupportedBinPackingSelectionHandler

  override def startSolution: TopLeftFirstBinPackingSolution = selectionHandler.startSolution
}

trait CandidateSupportedBinPackingSelectionHandler extends BinPackingSelectionHandler[TopLeftFirstBinPackingSolution] {

  val boxLength: Int

  override val startSolution: TopLeftFirstBinPackingSolution = TopLeftFirstBinPackingSolution.apply(boxLength)

  override def placeCandidateInSolution(
    candidate: Rectangle,
    solution: TopLeftFirstBinPackingSolution
  ): TopLeftFirstBinPackingSolution = {
    solution.placeTopLeftFirst(candidate)
  }

}
