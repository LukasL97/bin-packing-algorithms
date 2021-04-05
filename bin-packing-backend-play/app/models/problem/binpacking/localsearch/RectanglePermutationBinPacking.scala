package models.problem.binpacking.localsearch

import models.algorithm.Score
import models.problem.binpacking.BinPackingInstance
import models.problem.binpacking.localsearch.evaluation.BoxWeightedTopLeftFirstEvaluation
import models.problem.binpacking.localsearch.neighborhood.RectanglePermutationNeighborhood
import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.BinPackingSolutionRepresentation
import models.problem.binpacking.solution.BoxClosingTopLeftFirstBinPackingSolution
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.RectanglePermutationBinPackingSolutionRepresentation

import scala.collection.View

class RectanglePermutationBinPacking(
  override val instance: BinPackingInstance
) extends BinPackingLocalSearch[BoxClosingTopLeftFirstBinPackingSolution] {

  override val solutionHandler: BinPackingSolutionHandler[BoxClosingTopLeftFirstBinPackingSolution] =
    new RectanglePermutationBinPackingSolutionHandler(instance.rectangles, instance.boxLength)

  override def transformToStoredRepresentation(solution: BinPackingSolution): BinPackingSolutionRepresentation = {
    val rectanglePermutationSolution = solution.asInstanceOf[BoxClosingTopLeftFirstBinPackingSolution]
    RectanglePermutationBinPackingSolutionRepresentation(
      rectanglePermutationSolution.placement,
      rectanglePermutationSolution.update,
      rectanglePermutationSolution.rectangles.map(_.id)
    )
  }
}

class RectanglePermutationBinPackingSolutionHandler(
  rectangles: Seq[Rectangle],
  override val boxLength: Int
) extends BinPackingSolutionHandler[BoxClosingTopLeftFirstBinPackingSolution] with BoxWeightedTopLeftFirstEvaluation {

  override val startSolution: BoxClosingTopLeftFirstBinPackingSolution =
    BoxClosingTopLeftFirstBinPackingSolution(rectangles, boxLength)

  private val neighborhood = new RectanglePermutationNeighborhood(
    boxLength,
    targetBoxFillGrade = 1.0,
    consideredCandidatesAreaOvershootFactor = 2.0,
    consideredCandidatesAreaMaximumRelativeSize = 0.3
  )

  override def getNeighborhood(
    solution: BoxClosingTopLeftFirstBinPackingSolution,
    step: Int
  ): View[BoxClosingTopLeftFirstBinPackingSolution] = {
    neighborhood.createRectanglePermutationNeighborhood(solution)
  }

  override def evaluate(solution: BoxClosingTopLeftFirstBinPackingSolution, step: Int): Score = {
    evaluate(solution)
  }
}
