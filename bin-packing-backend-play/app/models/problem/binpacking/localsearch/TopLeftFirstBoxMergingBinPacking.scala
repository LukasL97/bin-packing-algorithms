package models.problem.binpacking.localsearch

import metrics.Metrics
import models.algorithm.Score
import models.problem.binpacking.localsearch.evaluation.BoxWeightedTopLeftFirstEvaluation
import models.problem.binpacking.localsearch.neighborhood.BoxReorderingNeighborhood
import models.problem.binpacking.localsearch.neighborhood.TopLeftFirstBoxPullUpNeighborhood
import models.problem.binpacking.localsearch.neighborhood.TopLeftFirstBoxMergeNeighborhood
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.TopLeftFirstBinPackingSolution

import scala.collection.View

class TopLeftFirstBoxMergingBinPacking(
  override val boxLength: Int,
  override val numRectangles: Int,
  override val rectangleWidthRange: (Int, Int),
  override val rectangleHeightRange: (Int, Int)
) extends BinPackingLocalSearch[TopLeftFirstBinPackingSolution] {

  override val solutionHandler: BinPackingSolutionHandler[TopLeftFirstBinPackingSolution] =
    new TopLeftFirstBoxMergingBinPackingSolutionHandler(rectangles, boxLength)

}

class TopLeftFirstBoxMergingBinPackingSolutionHandler(
  val rectangles: Set[Rectangle],
  override val boxLength: Int
) extends BinPackingSolutionHandler[TopLeftFirstBinPackingSolution] with BoxWeightedTopLeftFirstEvaluation
    with Metrics {

  override val startSolution: TopLeftFirstBinPackingSolution = withTimer("ls-merging-start-solution") {
    TopLeftFirstBinPackingSolution.apply(rectangles.toSeq, boxLength)
  }

  private val boxReorderingNeighborhood = new BoxReorderingNeighborhood[TopLeftFirstBinPackingSolution]
  private val boxPullUpNeighborhood =
    new TopLeftFirstBoxPullUpNeighborhood[TopLeftFirstBinPackingSolution](boxLength)
  private val boxMergeNeighborhood = new TopLeftFirstBoxMergeNeighborhood[TopLeftFirstBinPackingSolution](boxLength)

  override def getNeighborhood(solution: TopLeftFirstBinPackingSolution, step: Int): View[TopLeftFirstBinPackingSolution] = {
    val solutionsWithReorderedBoxes = boxReorderingNeighborhood.reorderBoxesByFillGrade(solution)
    val solutionsWithSingleBoxPullUp = boxPullUpNeighborhood.createSolutionsWithSingleBoxPullUp(solution)
    val solutionsWithMaximalBoxPullUp = boxPullUpNeighborhood.createSolutionsWithMaximalBoxPullUp(solution)
    val solutionsWithMergedBoxes = boxMergeNeighborhood.createSolutionsWithMergedBoxes(solution)
    solutionsWithReorderedBoxes ++
      solutionsWithMergedBoxes ++
      solutionsWithMaximalBoxPullUp ++
      solutionsWithSingleBoxPullUp
  }

  override def evaluate(solution: TopLeftFirstBinPackingSolution, step: Int): Score = {
    withTimer("ls-merging-evaluate") {
      evaluate(solution)
    }
  }
}
