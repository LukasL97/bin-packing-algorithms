package models.problem.binpacking.localsearch

import models.algorithm.Score
import models.problem.binpacking.localsearch.evaluation.BoxWeightedTopLeftFirstEvaluation
import models.problem.binpacking.localsearch.initialization.GeometricStartSolution
import models.problem.binpacking.localsearch.neighborhood.BoxMergeNeighborhood
import models.problem.binpacking.localsearch.neighborhood.BoxPullUpNeighborhood
import models.problem.binpacking.localsearch.neighborhood.CoarseMultipleBoxPullUpNeighborhood
import models.problem.binpacking.localsearch.neighborhood.GeometricShiftNeighborhood
import models.problem.binpacking.localsearch.neighborhood.Left
import models.problem.binpacking.localsearch.neighborhood.Up
import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.Rectangle

import scala.collection.View

class GeometryBasedBinPacking(
  override val boxLength: Int,
  override val numRectangles: Int,
  override val rectangleWidthRange: (Int, Int),
  override val rectangleHeightRange: (Int, Int)
) extends BinPackingLocalSearch {

  override val solutionHandler: BinPackingSolutionHandler =
    new GeometryBasedBinPackingSolutionHandler(rectangles, boxLength)

}

class GeometryBasedBinPackingSolutionHandler(
  override val rectangles: Set[Rectangle],
  override val boxLength: Int,
) extends BinPackingSolutionHandler with BoxWeightedTopLeftFirstEvaluation with GeometricStartSolution {

  override val startSolution: BinPackingSolution = triviallyFeasibleStartSolution

  private val boxPullUpNeighborhood = new BoxPullUpNeighborhood(boxLength)
  private val geometricShiftNeighborhood = new GeometricShiftNeighborhood(boxLength)
  private val boxMergeNeighborhood = new BoxMergeNeighborhood(rectangles, boxLength)
  private val coarseMultipleBoxPullUpNeighborhood = new CoarseMultipleBoxPullUpNeighborhood(boxLength)

  override def getNeighborhood(solution: BinPackingSolution): View[BinPackingSolution] = {
    val solutionsWithCoarseMultipleBoxPullUp =
      coarseMultipleBoxPullUpNeighborhood.createCoarseMultipleBoxPullUpNeighborhood(solution)
    val solutionsWithMergedBoxes = boxMergeNeighborhood.createBoxMergeNeighborhood(solution)
    val solutionsWithEntireBoxUpShift =
      geometricShiftNeighborhood.createEntireBoxMaximallyShiftedSolutions(solution, Up)
    val solutionsWithEntireBoxLeftShift =
      geometricShiftNeighborhood.createEntireBoxMaximallyShiftedSolutions(solution, Left)
    val solutionsWithBoxPullUp = boxPullUpNeighborhood.createBoxPullUpNeighborhood(solution)
    solutionsWithMergedBoxes ++
      solutionsWithCoarseMultipleBoxPullUp ++
      solutionsWithEntireBoxUpShift ++
      solutionsWithEntireBoxLeftShift ++
      solutionsWithBoxPullUp
  }

  override def evaluate(solution: BinPackingSolution, step: Int): Score = {
    evaluate(solution)
  }
}
