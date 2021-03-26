package models.problem.binpacking.localsearch.initialization

import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.SimpleBinPackingSolution

trait GeometricStartSolution {

  val rectangles: Set[Rectangle]
  val boxLength: Int

  private lazy val minRectangleWidth = rectangles.map(_.width).min
  private lazy val minRectangleHeight = rectangles.map(_.height).min

  private lazy val maxRectangleWidth = rectangles.map(_.width).max
  private lazy val maxRectangleHeight = rectangles.map(_.height).max

  def triviallyFeasibleStartSolution: SimpleBinPackingSolution = {
    createStartSolution(getStartCoordinates(maxRectangleWidth, maxRectangleHeight))
  }

  def overconfidentStartSolution: SimpleBinPackingSolution = {
    createStartSolution(
      getStartCoordinates(
        (maxRectangleWidth + minRectangleWidth) / 2,
        (maxRectangleHeight + minRectangleHeight) / 2
      )
    )
  }

  private def getStartCoordinates(horizontalStep: Int, verticalStep: Int): Seq[Coordinates] =
    for {
      x <- 0 to (boxLength - maxRectangleWidth) by horizontalStep
      y <- 0 to (boxLength - maxRectangleHeight) by verticalStep
    } yield Coordinates(x, y)

  private def createStartSolution(coordinates: Seq[Coordinates]): SimpleBinPackingSolution = {
    val placement = rectangles.zipWithIndex.map {
      case (rectangle, index) =>
        rectangle -> Placing(
          Box(index / coordinates.size + 1, boxLength),
          coordinates(index % coordinates.size)
        )
    }.toMap
    SimpleBinPackingSolution(placement)
  }

}
