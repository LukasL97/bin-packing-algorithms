package models.problem.binpacking.greedy

import models.problem.binpacking.Box
import models.problem.binpacking.Coordinates
import models.problem.binpacking.Placing
import models.problem.binpacking.Rectangle
import models.problem.binpacking.BinPackingSolution
import org.scalatest.MustMatchers
import org.scalatest.WordSpec

class BinPackingGreedySpec extends WordSpec with MustMatchers {

  "BinPackingGreedy" should {
    "place rectangles in separate boxes in the given candidate order" when {
      "given a selection strategy with sequential candidate selection and one box per rectangle placing strategy" in {

        val binPacking = new BinPackingGreedyImpl(10, 4, (3, 3), (3, 3))

        val finalSolution = binPacking.greedy.run()

        finalSolution.placement.keys.map(_.id).toSeq mustEqual binPacking.orderedRectangles.map(_.id)

        finalSolution.placement.foreach {
          case (rectangle, placing) =>
            rectangle.id mustEqual placing.box.id
        }
      }
    }
  }

}

private class BinPackingGreedyImpl(
  override val boxLength: Int = 10,
  override val numRectangles: Int = 4,
  override val rectangleWidthRange: (Int, Int) = (3, 3),
  override val rectangleHeightRange: (Int, Int) = (3, 3)
) extends BinPackingGreedy {

  val orderedRectangles: Seq[Rectangle] = rectangles.toSeq.sortBy(_.id)

  override val selectionHandler: BinPackingSelectionHandler = new BinPackingSelectionHandlerImpl(
    boxLength,
    orderedRectangles
  )
}

private class BinPackingSelectionHandlerImpl(
  override val boxLength: Int,
  override val candidates: Iterable[Rectangle]
) extends BinPackingSelectionHandler {

  override def selectNextCandidate(candidates: Iterable[Rectangle]): (Rectangle, Iterable[Rectangle]) = {
    (candidates.head, candidates.tail)
  }

  override def placeCandidateInSolution(
    candidate: Rectangle,
    solution: BinPackingSolution
  ): BinPackingSolution = {
    val maxBoxId = solution.placement.values.map(_.box.id).maxOption.getOrElse(0)
    BinPackingSolution(
      solution.placement + (candidate -> Placing(Box(maxBoxId + 1, boxLength, boxLength), Coordinates(0, 0)))
    )
  }
}
