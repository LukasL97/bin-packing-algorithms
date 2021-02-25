package models.problem.rectangles.greedy

import models.problem.rectangles.Box
import models.problem.rectangles.Coordinates
import models.problem.rectangles.Placing
import models.problem.rectangles.Rectangle
import models.problem.rectangles.RectanglesPlacementSolution
import org.scalatest.MustMatchers
import org.scalatest.WordSpec

class RectanglesPlacementGreedySpec extends WordSpec with MustMatchers {

  "RectanglesPlacementGreedy" should {
    "should place rectangles in separate boxes in the given candidate order" when {
      "given a selection strategy with sequential candidate selection and one box per rectangle placing strategy" in {

        val rectanglesPlacement = new RectanglesPlacementGreedyImpl(10, 4, (3, 3), (3, 3))

        val finalSolution = rectanglesPlacement.greedy.run()

        finalSolution.placement.keys.map(_.id).toSeq mustEqual rectanglesPlacement.orderedRectangles.map(_.id)

        finalSolution.placement.foreach {
          case (rectangle, placing) =>
            rectangle.id mustEqual placing.box.id
        }
      }
    }
  }

}

private class RectanglesPlacementGreedyImpl(
  override val boxLength: Int = 10,
  override val numRectangles: Int = 4,
  override val rectangleWidthRange: (Int, Int) = (3, 3),
  override val rectangleHeightRange: (Int, Int) = (3, 3)
) extends RectanglesPlacementGreedy {

  val orderedRectangles: Seq[Rectangle] = rectangles.toSeq.sortBy(_.id)

  override val selectionHandler: RectanglesPlacementSelectionHandler = new RectanglesPlacementSelectionHandlerImpl(
    boxLength,
    orderedRectangles
  )
}

private class RectanglesPlacementSelectionHandlerImpl(
  override val boxLength: Int,
  override val candidates: Iterable[Rectangle]
) extends RectanglesPlacementSelectionHandler {

  override def selectNextCandidate(candidates: Iterable[Rectangle]): (Rectangle, Iterable[Rectangle]) = {
    (candidates.head, candidates.tail)
  }

  override def placeCandidateInSolution(
    candidate: Rectangle,
    solution: RectanglesPlacementSolution
  ): RectanglesPlacementSolution = {
    val maxBoxId = solution.placement.values.map(_.box.id).maxOption.getOrElse(0)
    RectanglesPlacementSolution(
      solution.placement + (candidate -> Placing(Box(maxBoxId + 1, boxLength, boxLength), Coordinates(0, 0)))
    )
  }
}
