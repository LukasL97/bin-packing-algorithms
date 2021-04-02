package actors.dumpers.combining

import dao.CombinedBinPackingSolutionStepDAO
import models.problem.binpacking.BinPacking
import models.problem.binpacking.greedy.BinPackingGreedy

import javax.inject.Inject

class CombiningSolutionStepDumperProcessorProvider @Inject()(
  val dao: CombinedBinPackingSolutionStepDAO
) {

  def get(
    binPacking: BinPacking,
  ): Option[CombiningSolutionStepDumperProcessor] = binPacking match {
    case binPacking: BinPackingGreedy[_] =>
      Option(
        new GreedyCombiningSolutionStepDumperProcessor(dao, binPacking.instance)
      )
    case _ => Option.empty[CombiningSolutionStepDumperProcessor]
  }

}
