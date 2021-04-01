package models.problem.binpacking.greedy

import models.problem.binpacking.BinPackingInstance
import models.problem.binpacking.greedy.basic.BasicBinPackingGreedy
import models.problem.binpacking.greedy.basic.BasicBinPackingSelectionHandler
import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.SimpleBinPackingSolution
import org.scalatest.MustMatchers
import org.scalatest.WordSpec

class BasicBinPackingGreedySpec extends WordSpec with MustMatchers {

  "BasicBinPackingGreedy" should {
    "place rectangles in separate boxes in the given candidate order" when {
      "given a selection strategy with sequential candidate selection and one box per rectangle placing strategy" in {

        val binPacking = new BinPackingGreedyImpl(BinPackingInstance.apply(10, 4, 3, 3, 3, 3))

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
  override val instance: BinPackingInstance
) extends BasicBinPackingGreedy {

  val orderedRectangles: Seq[Rectangle] = instance.rectangles.sortBy(_.id)

  override val selectionHandler: BasicBinPackingSelectionHandler = new BinPackingSelectionHandlerImpl(
    instance.boxLength,
    orderedRectangles
  )
}

private class BinPackingSelectionHandlerImpl(
  override val boxLength: Int,
  override val candidates: Iterable[Rectangle]
) extends BasicBinPackingSelectionHandler {

  override def selectNextCandidate(
    candidates: Iterable[Rectangle],
    solution: SimpleBinPackingSolution
  ): (Rectangle, Iterable[Rectangle]) = {
    (candidates.head, candidates.tail)
  }

  override def placeCandidateInSolution(
    candidate: Rectangle,
    solution: SimpleBinPackingSolution
  ): SimpleBinPackingSolution = {
    val maxBoxId = solution.placement.values.map(_.box.id).maxOption.getOrElse(0)
    SimpleBinPackingSolution(
      solution.placement + (candidate -> Placing(Box(maxBoxId + 1, boxLength), Coordinates(0, 0)))
    )
  }
}
