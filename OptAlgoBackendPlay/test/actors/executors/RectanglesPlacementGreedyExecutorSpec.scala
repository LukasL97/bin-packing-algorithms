package actors.executors

import actors.RectanglesPlacementSolutionStep
import dao.RectanglesPlacementSolutionStepDAO
import models.problem.rectangles.Box
import models.problem.rectangles.Coordinates
import models.problem.rectangles.Placing
import models.problem.rectangles.Rectangle
import models.problem.rectangles.RectanglesPlacementSolution
import models.problem.rectangles.greedy.RectanglesPlacementGreedy
import models.problem.rectangles.greedy.RectanglesPlacementSelectionHandler
import org.scalamock.scalatest.MockFactory
import org.scalatest.WordSpec

class RectanglesPlacementGreedyExecutorSpec extends WordSpec with MockFactory {

  private class RectanglesPlacementGreedyImpl(
    override val boxLength: Int,
    override val numRectangles: Int,
    override val rectangleWidthRange: (Int, Int),
    override val rectangleHeightRange: (Int, Int)
  ) extends RectanglesPlacementGreedy {
    override val selectionHandler = new RectanglesPlacementSelectionHandlerImpl(boxLength, rectangles)
  }

  private class RectanglesPlacementSelectionHandlerImpl(
    override val boxLength: Int,
    override val candidates: Iterable[Rectangle]
  ) extends RectanglesPlacementSelectionHandler {
    override def selectNextCandidate(candidates: Iterable[Rectangle]): (Rectangle, Iterable[Rectangle]) = {
      (candidates.head, candidates.tail)
    }
  }

  private val dao = mock[RectanglesPlacementSolutionStepDAO]
  private val executor = new RectanglesPlacementGreedyExecutor(dao)

  "RectanglesPlacementGreedyExecutor" should {
    "dump intermediate solution steps correctly" when {
      "receiving a rectanglesPlacement with three rectangles to place" in {
        val runId = "runId"

        val boxLength = 6
        val numRectangles = 3
        val rectanglesWidthRange = (3, 3)
        val rectanglesHeightRange = (2, 2)

        val box = Box(1, boxLength, boxLength)

        val rectanglesPlacement = new RectanglesPlacementGreedyImpl(
          boxLength,
          numRectangles,
          rectanglesWidthRange,
          rectanglesHeightRange
        )

        (dao.dumpSolutionStep _)
          .expects(
            RectanglesPlacementSolutionStep(
              runId,
              0,
              RectanglesPlacementSolution(
                Map()
              )
            )
          )
          .returns(null)
        (dao.dumpSolutionStep _)
          .expects(
            RectanglesPlacementSolutionStep(
              runId,
              1,
              RectanglesPlacementSolution(
                Map(
                  rectanglesPlacement.rectangles.toSeq(0) -> Placing(box, Coordinates(0, 0))
                )
              )
            )
          )
          .returns(null)
        (dao.dumpSolutionStep _)
          .expects(
            RectanglesPlacementSolutionStep(
              runId,
              2,
              RectanglesPlacementSolution(
                Map(
                  rectanglesPlacement.rectangles.toSeq(0) -> Placing(box, Coordinates(0, 0)),
                  rectanglesPlacement.rectangles.toSeq(1) -> Placing(box, Coordinates(0, 2))
                )
              )
            )
          )
          .returns(null)
        (dao.dumpSolutionStep _)
          .expects(
            RectanglesPlacementSolutionStep(
              runId,
              3,
              RectanglesPlacementSolution(
                Map(
                  rectanglesPlacement.rectangles.toSeq(0) -> Placing(box, Coordinates(0, 0)),
                  rectanglesPlacement.rectangles.toSeq(1) -> Placing(box, Coordinates(0, 2)),
                  rectanglesPlacement.rectangles.toSeq(2) -> Placing(box, Coordinates(3, 0))
                )
              )
            )
          )
          .returns(null)
        (dao.dumpSolutionStep _)
          .expects(
            RectanglesPlacementSolutionStep(
              runId,
              4,
              RectanglesPlacementSolution(
                Map(
                  rectanglesPlacement.rectangles.toSeq(0) -> Placing(box, Coordinates(0, 0)),
                  rectanglesPlacement.rectangles.toSeq(1) -> Placing(box, Coordinates(0, 2)),
                  rectanglesPlacement.rectangles.toSeq(2) -> Placing(box, Coordinates(3, 0))
                )
              ),
              finished = true
            )
          )
          .returns(null)

        executor.execute(runId, rectanglesPlacement)

      }
    }
  }

}
