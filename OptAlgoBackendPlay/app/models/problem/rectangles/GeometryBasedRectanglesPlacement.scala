package models.problem.rectangles

import play.api.Logging

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
) extends RectanglesPlacementSolutionHandler with Logging {

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
    val solutionsWithBoxPullUp = solution.placement.collect {
      case (rectangle, (Box(id, width, height), (x, y))) if id > 1 =>
        RectanglesPlacementSolution(
          solution.placement.updated(
            rectangle,
            (Box(id - 1, width, height), (width - rectangle.width, height - rectangle.height))
          )
        )
    }.toSet
    val solutionsWithUpShift = solution.placement.map {
      case (rectangle, (box, (x, y))) =>
        RectanglesPlacementSolution(
          solution.placement.updated(
            rectangle,
            (box, (x, y - 1))
          )
        )
    }.toSet
    val solutionsWithLeftShift = solution.placement.map {
      case (rectangle, (box, (x, y))) =>
        RectanglesPlacementSolution(
          solution.placement.updated(
            rectangle,
            (box, (x - 1, y))
          )
        )
    }.toSet
    val neighborhood = (solutionsWithBoxPullUp ++ solutionsWithUpShift ++ solutionsWithLeftShift).filter(isFeasible)
    logger.trace(s"Get neighborhood of size ${neighborhood.size}")
    neighborhood
  }

  override def evaluate(solution: RectanglesPlacementSolution): Double = {
    val boxWithMaxId = solution.placement.map {
      case (rectangle, (box, coordinates)) => box
    }.max((x: Box, y: Box) => x.id - y.id)
    val numBoxesUsed = boxWithMaxId.id
    val rectanglesInBoxWithMaxId = solution.placement.collect {
      case (rectangle, (box, coordinates)) if box == boxWithMaxId => rectangle -> coordinates
    }
    val rightmostRectangleInBoxWithMaxId = rectanglesInBoxWithMaxId.max(
      new Ordering[(Rectangle, (Int, Int))]() {
        override def compare(x: (Rectangle, (Int, Int)), y: (Rectangle, (Int, Int))): Int =
          (x._1.width + x._2._1) - (y._1.width + y._2._1)
      }
    )
    val rightmostPointInBoxWithMaxId = rightmostRectangleInBoxWithMaxId._1.width + rightmostRectangleInBoxWithMaxId._2._1
    val bottommostRectangleInBoxWithMaxId = rectanglesInBoxWithMaxId.max(
      new Ordering[(Rectangle, (Int, Int))]() {
        override def compare(x: (Rectangle, (Int, Int)), y: (Rectangle, (Int, Int))): Int =
          (x._1.height + x._2._2) - (y._1.height + y._2._2)
      }
    )
    val bottommostPointInBoxWithMaxId = bottommostRectangleInBoxWithMaxId._1.height + bottommostRectangleInBoxWithMaxId._2._2
    val boxWithMaxIdFillRatio = (rightmostPointInBoxWithMaxId * bottommostPointInBoxWithMaxId).toFloat / (boxWithMaxId.width * boxWithMaxId.height)
    numBoxesUsed - 1 + boxWithMaxIdFillRatio
  }
}
