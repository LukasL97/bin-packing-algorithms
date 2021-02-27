package models.problem.rectangles.greedy

import models.problem.rectangles.Box
import models.problem.rectangles.Coordinates
import models.problem.rectangles.Placing
import models.problem.rectangles.Rectangle
import models.problem.rectangles.RectanglesPlacementSolution
import org.scalatest.MustMatchers
import org.scalatest.WordSpec

class RectanglesPlacementSelectionHandlerSpec extends WordSpec with MustMatchers {

  private class RectanglesPlacementSelectionHandlerImpl(
    override val boxLength: Int,
    override val candidates: Iterable[Rectangle]
  ) extends RectanglesPlacementSelectionHandler {
    override def selectNextCandidate(candidates: Iterable[Rectangle]): (Rectangle, Iterable[Rectangle]) = {
      (candidates.head, candidates.tail)
    }
  }

  private val boxLength = 5
  private val selectionHandler = new RectanglesPlacementSelectionHandlerImpl(boxLength, Seq.empty)

  "RectanglesPlacementSelectionHandler" should {

    "place a candidate in the first possible position according to the top left strategy" when {

      val solution = RectanglesPlacementSolution(Map(
        Rectangle(1, 5, 3) -> Placing(Box(1, 5, 5), Coordinates(0, 0)),
        Rectangle(2, 3, 2) -> Placing(Box(1, 5, 5), Coordinates(0, 3)),
        Rectangle(3, 2, 1) -> Placing(Box(2, 5, 5), Coordinates(0, 0))
      ))

      "given a candidate fitting in the first box" in {
        val candidate = Rectangle(4, 1, 2)
        selectionHandler.placeCandidateInSolution(candidate, solution).placement mustEqual {
          solution.placement + (candidate -> Placing(Box(1 ,boxLength ,boxLength), Coordinates(3, 3)))
        }
      }

      "given a candidate not fitting in the first but in the second box" in {
        val candidate = Rectangle(4, 3, 2)
        selectionHandler.placeCandidateInSolution(candidate, solution).placement mustEqual {
          solution.placement + (candidate -> Placing(Box(2 ,boxLength ,boxLength), Coordinates(0, 1)))
        }
      }

      "given a candidate not fitting in any currently used box" in {
        val candidate = Rectangle(4, 4, 5)
        selectionHandler.placeCandidateInSolution(candidate, solution).placement mustEqual {
          solution.placement + (candidate -> Placing(Box(3 ,boxLength ,boxLength), Coordinates(0, 0)))
        }
      }
    }

    "place a candidate in box 1 in the top left corner according to the top left strategy" when {
      "given a solution without any candidate placed previously" in {
        val candidate = Rectangle(1, 3, 3)
        val solution = RectanglesPlacementSolution(Map())
        selectionHandler.placeCandidateInSolution(candidate, solution).placement mustEqual {
          Map(candidate -> Placing(Box(1 ,boxLength ,boxLength), Coordinates(0, 0)))
        }
      }
    }
  }

}
