package models.problem.rectangles

class GeometryBasedRectanglesPlacement(
  override val boxLength: Int,
  override val numRectangles: Int,
  override val rectangleWidthRange: (Int, Int),
  override val rectangleHeightRange: (Int, Int)
) extends RectanglesPlacement {

  override val solutionHandler: RectanglesPlacementSolutionHandler =
    new GeometryBasedRectanglesPlacementSolutionHandler(boxes, rectangles)

  private val thisSolutionHandler = solutionHandler

  override val localSearch: RectanglesPlacementLocalSearch = new RectanglesPlacementLocalSearch {
    override val solutionHandler: RectanglesPlacementSolutionHandler = thisSolutionHandler
  }

}

class GeometryBasedRectanglesPlacementSolutionHandler(
  boxes: Set[Box],
  rectangles: Set[Rectangle]
) extends RectanglesPlacementSolutionHandler {

  override def createArbitraryFeasibleSolution(): RectanglesPlacementSolution = {
    require(boxes.size == rectangles.size)
    val solution = RectanglesPlacementSolution(
      (rectangles zip boxes).map {
        case (rectangle, box) => rectangle -> (box, (0, 0))
      }.toMap
    )
    if (isFeasible(solution)) {
      solution
    } else {
      throw new RuntimeException("Created infeasible solution as starting solution")
    }
  }

  override def getNeighborhood(solution: RectanglesPlacementSolution): Set[RectanglesPlacementSolution] = {
    // TODO
    Set(solution)
  }

  override def evaluate(solution: RectanglesPlacementSolution): Double = {
    // TODO
    0.0
  }
}
