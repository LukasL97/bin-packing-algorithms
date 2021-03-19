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
) extends BinPackingSolutionHandler with BoxPullUpNeighborhood with GeometricShiftNeighborhood
    with BoxMergeNeighborhood with CoarseMultipleBoxPullUpNeighborhood with BoxWeightedTopLeftFirstEvaluation
    with GeometricStartSolution {

  override val startSolution: BinPackingSolution = triviallyFeasibleStartSolution

  override def getNeighborhood(solution: BinPackingSolution): View[BinPackingSolution] = {
    val solutionsWithCoarseMultipleBoxPullUp = createCoarseMultipleBoxPullUpNeighborhood(solution)
    val solutionsWithMergedBoxes = createBoxMergeNeighborhood(solution)
    val solutionsWithEntireBoxUpShift = createEntireBoxMaximallyShiftedSolutions(solution, Up)
    val solutionsWithEntireBoxLeftShift = createEntireBoxMaximallyShiftedSolutions(solution, Left)
    val solutionsWithBoxPullUp = createBoxPullUpNeighborhood(solution)
    //val solutionsWithSingleUpShift = createMaximallyShiftedSolutions(solution, Up)
    //val solutionsWithSingleLeftShift = createMaximallyShiftedSolutions(solution, Left)
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
