package models.problem.binpacking.localsearch

import metrics.Metrics
import models.algorithm.Score
import models.problem.binpacking.BinPackingInstance
import models.problem.binpacking.localsearch.evaluation.BoxWeightedTopLeftFirstEvaluation
import models.problem.binpacking.localsearch.initialization.GeometricStartSolution
import models.problem.binpacking.localsearch.neighborhood.BoxMergeNeighborhood
import models.problem.binpacking.localsearch.neighborhood.BoxPullUpNeighborhood
import models.problem.binpacking.localsearch.neighborhood.BoxReorderingNeighborhood
import models.problem.binpacking.localsearch.neighborhood.CoarseMultipleBoxPullUpNeighborhood
import models.problem.binpacking.localsearch.neighborhood.GeometricShiftNeighborhood
import models.problem.binpacking.localsearch.neighborhood.Left
import models.problem.binpacking.localsearch.neighborhood.Up
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.SimpleBinPackingSolution

import scala.collection.View

class GeometryBasedBinPacking(
  override val instance: BinPackingInstance
) extends BinPackingLocalSearch[SimpleBinPackingSolution] {

  override val solutionHandler: BinPackingSolutionHandler[SimpleBinPackingSolution] =
    new GeometryBasedBinPackingSolutionHandler(instance.rectangles.toSet, instance.boxLength)

}

class GeometryBasedBinPackingSolutionHandler(
  override val rectangles: Set[Rectangle],
  override val boxLength: Int
) extends BinPackingSolutionHandler[SimpleBinPackingSolution] with BoxWeightedTopLeftFirstEvaluation
    with GeometricStartSolution with Metrics {

  override val startSolution: SimpleBinPackingSolution = triviallyFeasibleStartSolution

  private val boxPullUpNeighborhood = new BoxPullUpNeighborhood[SimpleBinPackingSolution](boxLength)
  private val geometricShiftNeighborhood = new GeometricShiftNeighborhood[SimpleBinPackingSolution](boxLength)
  private val boxMergeNeighborhood = new BoxMergeNeighborhood[SimpleBinPackingSolution](rectangles, boxLength)
  private val coarseMultipleBoxPullUpNeighborhood =
    new CoarseMultipleBoxPullUpNeighborhood[SimpleBinPackingSolution](boxLength)
  private val boxReorderingNeighborhood = new BoxReorderingNeighborhood[SimpleBinPackingSolution]

  override def getNeighborhood(solution: SimpleBinPackingSolution, step: Int): View[SimpleBinPackingSolution] = {
    val solutionsWithCoarseMultipleBoxPullUp =
      coarseMultipleBoxPullUpNeighborhood.createCoarseMultipleBoxPullUpNeighborhood(solution)
    val solutionsWithMergedBoxes = boxMergeNeighborhood.createBoxMergeNeighborhood(solution)
    val solutionsWithEntireBoxUpShift =
      geometricShiftNeighborhood.createEntireBoxMaximallyShiftedSolutions(solution, Up)
    val solutionsWithEntireBoxLeftShift =
      geometricShiftNeighborhood.createEntireBoxMaximallyShiftedSolutions(solution, Left)
    val solutionsWithBoxPullUp = boxPullUpNeighborhood.createBoxPullUpNeighborhood(solution)
    val solutionsWithReorderedBoxes = boxReorderingNeighborhood.reorderBoxesByFillGrade(solution)
    solutionsWithMergedBoxes ++
      solutionsWithReorderedBoxes ++
      solutionsWithCoarseMultipleBoxPullUp ++
      solutionsWithEntireBoxUpShift ++
      solutionsWithEntireBoxLeftShift ++
      solutionsWithBoxPullUp
  }

  override def evaluate(solution: SimpleBinPackingSolution, step: Int): Score = withTimer("ls-geometry-evaluate") {
    evaluate(solution)
  }
}
