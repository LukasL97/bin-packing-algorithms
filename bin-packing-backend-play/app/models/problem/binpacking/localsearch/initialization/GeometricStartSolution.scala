package models.problem.binpacking.localsearch.initialization

import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.SimpleBinPackingSolution

trait GeometricStartSolution {

  val rectangles: Set[Rectangle]
  val boxLength: Int

  private lazy val maxRectangleWidth = rectangles.map(_.width).max
  private lazy val maxRectangleHeight = rectangles.map(_.height).max

  private lazy val startCoordinates: Seq[Coordinates] = {
    for {
      x <- 0 to (boxLength - maxRectangleWidth) by maxRectangleWidth
      y <- 0 to (boxLength - maxRectangleHeight) by maxRectangleHeight
    } yield Coordinates(x, y)
  }

  def triviallyFeasibleStartSolution: BinPackingSolution = {
    val placement = rectangles.zipWithIndex.map {
      case (rectangle, index) =>
        rectangle -> Placing(
          Box(index / startCoordinates.size + 1, boxLength),
          startCoordinates(index % startCoordinates.size)
        )
    }.toMap
    SimpleBinPackingSolution(placement)
  }

}
