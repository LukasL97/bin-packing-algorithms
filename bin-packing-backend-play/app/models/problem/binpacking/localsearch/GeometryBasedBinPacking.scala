package models.problem.binpacking.localsearch

import models.algorithm.OneDimensionalScore
import models.algorithm.Score
import models.problem.binpacking.localsearch.evaluation.BoxWeightedTopLeftFirstEvaluation
import models.problem.binpacking.localsearch.neighborhood.BoxPullUpNeighborhood
import models.problem.binpacking.localsearch.neighborhood.GeometricShiftNeighborhood
import models.problem.binpacking.localsearch.neighborhood.Left
import models.problem.binpacking.localsearch.neighborhood.Up
import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.SimpleBinPackingSolution

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
  val rectangles: Set[Rectangle],
  val boxLength: Int,
) extends BinPackingSolutionHandler with BoxPullUpNeighborhood with GeometricShiftNeighborhood
    with BoxWeightedTopLeftFirstEvaluation {

  override val startSolution: BinPackingSolution = {
    val solution = SimpleBinPackingSolution(
      rectangles
        .map(
          rectangle =>
            rectangle -> Placing(
              Box(rectangle.id, boxLength),
              Coordinates(0, 0)
          )
        )
        .toMap
    )
    if (isFeasible(solution)) {
      solution
    } else {
      throw new RuntimeException("Created infeasible solution as starting solution")
    }
  }

  override def getNeighborhood(solution: BinPackingSolution): Set[BinPackingSolution] = {
    val solutionsWithBoxPullUp = createBoxPullUpNeighborhood(solution)
    val solutionsWithUpShift = createMaximallyShiftedSolutions(solution, Up)
    val solutionsWithLeftShift = createMaximallyShiftedSolutions(solution, Left)
    solutionsWithBoxPullUp ++ solutionsWithUpShift ++ solutionsWithLeftShift
  }

  override def evaluate(solution: BinPackingSolution, step: Int): Score = {
    OneDimensionalScore(evaluate(solution))
  }
}
