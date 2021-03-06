package models.problem.binpacking.greedy

import models.problem.binpacking.greedy.basic.BasicBinPackingSelectionHandler
import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.SimpleBinPackingSolution
import org.scalatest.MustMatchers
import org.scalatest.WordSpec

class BasicBinPackingSelectionHandlerSpec extends WordSpec with MustMatchers {

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
  }

  private val boxLength = 5
  private val selectionHandler = new BinPackingSelectionHandlerImpl(boxLength, Seq.empty)

  "BinPackingSelectionHandler" should {

    "place a candidate in the first possible position according to the top left strategy" when {

      val solution = SimpleBinPackingSolution(
        Map(
          Rectangle(1, 5, 3) -> Placing(Box(1, 5), Coordinates(0, 0)),
          Rectangle(2, 3, 2) -> Placing(Box(1, 5), Coordinates(0, 3)),
          Rectangle(3, 2, 1) -> Placing(Box(2, 5), Coordinates(0, 0))
        )
      )

      "given a candidate fitting in the first box" in {
        val candidate = Rectangle(4, 1, 2)
        selectionHandler.placeCandidateInSolution(candidate, solution).placement mustEqual {
          solution.placement + (candidate -> Placing(Box(1, boxLength), Coordinates(3, 3)))
        }
      }

      "given a candidate not fitting in the first but in the second box" in {
        val candidate = Rectangle(4, 3, 2)
        selectionHandler.placeCandidateInSolution(candidate, solution).placement mustEqual {
          solution.placement + (candidate -> Placing(Box(2, boxLength), Coordinates(0, 1)))
        }
      }

      "given a candidate not fitting in any currently used box" in {
        val candidate = Rectangle(4, 5, 5)
        selectionHandler.placeCandidateInSolution(candidate, solution).placement mustEqual {
          solution.placement + (candidate -> Placing(Box(3, boxLength), Coordinates(0, 0)))
        }
      }
    }

    "place a candidate in box 1 in the top left corner according to the top left strategy" when {
      "given a solution without any candidate placed previously" in {
        val candidate = Rectangle(1, 3, 3)
        val solution = SimpleBinPackingSolution.apply(boxLength)
        selectionHandler.placeCandidateInSolution(candidate, solution).placement mustEqual {
          Map(candidate -> Placing(Box(1, boxLength), Coordinates(0, 0)))
        }
      }
    }
  }

}
