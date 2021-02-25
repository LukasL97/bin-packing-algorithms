package models.problem.rectangles.greedy

import models.algorithm.Greedy
import models.algorithm.SelectionHandler
import models.problem.rectangles.Box
import models.problem.rectangles.Coordinates
import models.problem.rectangles.Placing
import models.problem.rectangles.Rectangle
import models.problem.rectangles.RectanglesPlacement
import models.problem.rectangles.RectanglesPlacementSolution

trait RectanglesPlacementGreedy extends RectanglesPlacement {
  val selectionHandler: RectanglesPlacementSelectionHandler
  lazy val greedy = new Greedy[Rectangle, RectanglesPlacementSolution](selectionHandler)
}

trait RectanglesPlacementSelectionHandler extends SelectionHandler[Rectangle, RectanglesPlacementSolution] {

  val boxLength: Int

  override val startSolution: RectanglesPlacementSolution = RectanglesPlacementSolution(Map())

  override def placeCandidateInSolution(
    candidate: Rectangle,
    solution: RectanglesPlacementSolution
  ): RectanglesPlacementSolution = {
    // TODO: proper placing algorithm
    val maxBoxId = solution.placement.values.map(_.box.id).max
    RectanglesPlacementSolution(
      solution.placement + (candidate -> Placing(Box(maxBoxId + 1, boxLength, boxLength), Coordinates(0, 0)))
    )
  }

}
