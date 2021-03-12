package actors.executors

import actors.BinPackingSolutionStep
import dao.BinPackingSolutionStepDAO
import models.problem.binpacking.Box
import models.problem.binpacking.Coordinates
import models.problem.binpacking.Placing
import models.problem.binpacking.Rectangle
import models.problem.binpacking.SimpleBinPackingSolution
import models.problem.binpacking.greedy.BinPackingGreedy
import models.problem.binpacking.greedy.BinPackingSelectionHandler
import org.scalamock.scalatest.MockFactory
import org.scalatest.WordSpec

class BinPackingGreedyExecutorSpec extends WordSpec with MockFactory {

  private class BinPackingGreedyImpl(
    override val boxLength: Int,
    override val numRectangles: Int,
    override val rectangleWidthRange: (Int, Int),
    override val rectangleHeightRange: (Int, Int)
  ) extends BinPackingGreedy {
    override val selectionHandler = new BinPackingSelectionHandlerImpl(boxLength, rectangles)
  }

  private class BinPackingSelectionHandlerImpl(
    override val boxLength: Int,
    override val candidates: Iterable[Rectangle]
  ) extends BinPackingSelectionHandler {
    override def selectNextCandidate(candidates: Iterable[Rectangle]): (Rectangle, Iterable[Rectangle]) = {
      (candidates.head, candidates.tail)
    }
  }

  private val dao = mock[BinPackingSolutionStepDAO]
  private val executor = new BinPackingGreedyExecutor(dao)

  "BinPackingGreedyExecutor" should {
    "dump intermediate solution steps correctly" when {
      "receiving a binPacking with three rectangles to place" in {
        val runId = "runId"

        val boxLength = 6
        val numRectangles = 3
        val rectanglesWidthRange = (3, 3)
        val rectanglesHeightRange = (2, 2)

        val box = Box(1, boxLength)

        val binPacking = new BinPackingGreedyImpl(
          boxLength,
          numRectangles,
          rectanglesWidthRange,
          rectanglesHeightRange
        )

        (dao.dumpSolutionStep _)
          .expects(
            BinPackingSolutionStep(
              runId,
              0,
              SimpleBinPackingSolution(
                Map()
              )
            )
          )
          .returns(null)
        (dao.dumpSolutionStep _)
          .expects(
            BinPackingSolutionStep(
              runId,
              1,
              SimpleBinPackingSolution(
                Map(
                  binPacking.rectangles.toSeq(0) -> Placing(box, Coordinates(0, 0))
                )
              )
            )
          )
          .returns(null)
        (dao.dumpSolutionStep _)
          .expects(
            BinPackingSolutionStep(
              runId,
              2,
              SimpleBinPackingSolution(
                Map(
                  binPacking.rectangles.toSeq(0) -> Placing(box, Coordinates(0, 0)),
                  binPacking.rectangles.toSeq(1) -> Placing(box, Coordinates(0, 2))
                )
              )
            )
          )
          .returns(null)
        (dao.dumpSolutionStep _)
          .expects(
            BinPackingSolutionStep(
              runId,
              3,
              SimpleBinPackingSolution(
                Map(
                  binPacking.rectangles.toSeq(0) -> Placing(box, Coordinates(0, 0)),
                  binPacking.rectangles.toSeq(1) -> Placing(box, Coordinates(0, 2)),
                  binPacking.rectangles.toSeq(2) -> Placing(box, Coordinates(3, 0))
                )
              )
            )
          )
          .returns(null)
        (dao.dumpSolutionStep _)
          .expects(
            BinPackingSolutionStep(
              runId,
              4,
              SimpleBinPackingSolution(
                Map(
                  binPacking.rectangles.toSeq(0) -> Placing(box, Coordinates(0, 0)),
                  binPacking.rectangles.toSeq(1) -> Placing(box, Coordinates(0, 2)),
                  binPacking.rectangles.toSeq(2) -> Placing(box, Coordinates(3, 0))
                )
              ),
              finished = true
            )
          )
          .returns(null)

        executor.execute(runId, binPacking)

      }
    }
  }

}
