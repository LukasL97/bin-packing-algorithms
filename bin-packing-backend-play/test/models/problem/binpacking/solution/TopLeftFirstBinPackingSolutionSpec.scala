package models.problem.binpacking.solution

import org.scalatest.MustMatchers
import org.scalatest.WordSpec

class TopLeftFirstBinPackingSolutionSpec extends WordSpec with MustMatchers {

  private val boxLength = 10

  "TopLeftFirstBinPackingSolution" should {

    "sort in new candidate" when {
      val solution = TopLeftFirstBinPackingSolution(boxLength)
      val candidates = Seq(
        TopLeftCandidate(Coordinates(2, 3), Set()),
        TopLeftCandidate(Coordinates(1, 6), Set())
      )

      "given a candidate smaller than all existing ones" in {
        val newCandidate = TopLeftCandidate(Coordinates(1, 1), Set())
        solution.sortInCandidate(candidates, newCandidate) mustEqual Seq(newCandidate) ++ candidates
      }
      "given a candidates larger than all existing ones" in {
        val newCandidate = TopLeftCandidate(Coordinates(6, 3), Set())
        solution.sortInCandidate(candidates, newCandidate) mustEqual candidates ++ Seq(newCandidate)
      }
      "given a candidates between existing candidates" in {
        val newCandidate = TopLeftCandidate(Coordinates(4, 2), Set())
        solution.sortInCandidate(candidates, newCandidate) mustEqual Seq(
          candidates.head,
          newCandidate,
          candidates.last
        )
      }
    }

    "get new candidate from rectangle bottom left" when {
      val solution = TopLeftFirstBinPackingSolution(boxLength)

      "the nearest right edge is the box left border" in {
        val rectangleBottomLeft = Coordinates(4, 4)
        val placement = Map(
          Rectangle(1, 2, 3) -> Coordinates(0, 0),
          Rectangle(2, 2, 2) -> Coordinates(2, 0),
          Rectangle(3, 2, 6) -> Coordinates(8, 0)
        )
        solution.getNewCandidateFromRectangleBottomLeft(rectangleBottomLeft, placement) mustBe Option(
          TopLeftCandidate(Coordinates(0, 4), Set(rectangleBottomLeft))
        )
      }
      "the nearest right edge is from some other rectangle" in {
        val rectangleBottomLeft = Coordinates(4, 4)
        val placement = Map(
          Rectangle(1, 2, 6) -> Coordinates(0, 0),
          Rectangle(2, 2, 2) -> Coordinates(2, 0),
          Rectangle(3, 2, 6) -> Coordinates(8, 0)
        )
        solution.getNewCandidateFromRectangleBottomLeft(rectangleBottomLeft, placement) mustBe Option(
          TopLeftCandidate(Coordinates(2, 4), Set(rectangleBottomLeft))
        )
      }
      "the nearest right edge contains the rectangle bottom left" in {
        val rectangleBottomLeft = Coordinates(4, 4)
        val placement = Map(
          Rectangle(1, 2, 6) -> Coordinates(0, 0),
          Rectangle(2, 2, 5) -> Coordinates(2, 0),
          Rectangle(3, 2, 6) -> Coordinates(8, 0)
        )
        solution.getNewCandidateFromRectangleBottomLeft(rectangleBottomLeft, placement) mustBe Option(
          TopLeftCandidate(rectangleBottomLeft, Set(rectangleBottomLeft))
        )
      }
      "a right edge contains the rectangle bottom left but not in the inside" in {
        val rectangleBottomLeft = Coordinates(4, 4)
        val placement = Map(
          Rectangle(1, 2, 6) -> Coordinates(0, 0),
          Rectangle(2, 2, 4) -> Coordinates(2, 0),
          Rectangle(3, 2, 6) -> Coordinates(8, 0)
        )
        solution.getNewCandidateFromRectangleBottomLeft(rectangleBottomLeft, placement) mustBe Option(
          TopLeftCandidate(Coordinates(2, 4), Set(rectangleBottomLeft))
        )
      }
    }

    "get new candidate from rectangle top right" when {
      val solution = TopLeftFirstBinPackingSolution(boxLength)

      "the nearest bottom edge is the box top border" in {
        val rectangleTopRight = Coordinates(2, 0)
        val placement = Map(
          Rectangle(1, 2, 2) -> Coordinates(0, 0)
        )
        solution.getNewCandidateFromRectangleTopRight(rectangleTopRight, placement) mustBe Option(
          TopLeftCandidate(rectangleTopRight, Set(rectangleTopRight))
        )
      }
      "the nearest bottom edge is from some other rectangle" in {
        val rectangleTopRight = Coordinates(4, 6)
        val placement = Map(
          Rectangle(1, 5, 2) -> Coordinates(0, 0),
          Rectangle(2, 2, 2) -> Coordinates(0, 2),
          Rectangle(3, 2, 2) -> Coordinates(0, 4)
        )
        solution.getNewCandidateFromRectangleTopRight(rectangleTopRight, placement) mustBe Option(
          TopLeftCandidate(Coordinates(4, 2), Set(rectangleTopRight))
        )
      }
    }

    "place a rectangle with top left first strategy" when {
      "placing it into an empty solution" in {
        val solution = TopLeftFirstBinPackingSolution(boxLength)
        val rectangle = Rectangle(1, 3, 4)
        solution.placeTopLeftFirst(rectangle) mustEqual TopLeftFirstBinPackingSolution(
          Map(
            rectangle -> Placing(Box(1, boxLength), Coordinates(0, 0))
          ),
          Map(
            1 -> Seq(
              TopLeftCandidate(Coordinates(3, 0), Set(Coordinates(3, 0))),
              TopLeftCandidate(Coordinates(0, 4), Set(Coordinates(0, 4)))
            )
          ),
          boxLength
        )
      }
      "placing it into a solution with some existing rectangles" in {
        val placement = Map(
          Rectangle(1, 6, 2) -> Placing(Box(1, boxLength), Coordinates(0, 0)),
          Rectangle(2, 3, 5) -> Placing(Box(1, boxLength), Coordinates(0, 2))
        )
        val candidates = Map(
          1 -> Seq(
            TopLeftCandidate(Coordinates(3, 2), Set(Coordinates(3, 2))),
            TopLeftCandidate(Coordinates(6, 0), Set(Coordinates(6, 0))),
            TopLeftCandidate(Coordinates(0, 7), Set(Coordinates(0, 7)))
          )
        )
        val solution = TopLeftFirstBinPackingSolution(placement, candidates, boxLength)
        val rectangle = Rectangle(3, 5, 1)
        solution.placeTopLeftFirst(rectangle) mustEqual TopLeftFirstBinPackingSolution(
          placement.updated(rectangle, Placing(Box(1, boxLength), Coordinates(3, 2))),
          Map(
            1 -> Seq(
              TopLeftCandidate(Coordinates(3, 3), Set(Coordinates(3, 3))),
              TopLeftCandidate(Coordinates(6, 0), Set(Coordinates(6, 0))),
              TopLeftCandidate(Coordinates(0, 7), Set(Coordinates(0, 7))),
              TopLeftCandidate(Coordinates(8, 0), Set(Coordinates(8, 2)))
            )
          ),
          boxLength
        )
      }
      "placing it into a solution so that a new candidate appears at its bottom edge" in {
        val placement = Map(
          Rectangle(1, 1, 1) -> Placing(Box(1, boxLength), Coordinates(0, 0)),
          Rectangle(2, 2, 1) -> Placing(Box(1, boxLength), Coordinates(0, 1))
        )
        val candidates = Map(
          1 -> Seq(
            TopLeftCandidate(Coordinates(1, 0), Set(Coordinates(1, 0))),
            TopLeftCandidate(Coordinates(2, 0), Set(Coordinates(2, 1))),
            TopLeftCandidate(Coordinates(0, 2), Set(Coordinates(0, 2)))
          )
        )
        val solution = TopLeftFirstBinPackingSolution(placement, candidates, boxLength)
        val rectangle = Rectangle(3, 2, 1)
        solution.placeTopLeftFirst(rectangle) mustEqual TopLeftFirstBinPackingSolution(
          placement.updated(rectangle, Placing(Box(1, boxLength), Coordinates(1, 0))),
          Map(
            1 -> Seq(
              TopLeftCandidate(Coordinates(0, 2), Set(Coordinates(0, 2))),
              TopLeftCandidate(Coordinates(3, 0), Set(Coordinates(3, 0))),
              TopLeftCandidate(Coordinates(2, 1), Set(Coordinates(2, 1)))
            )
          ),
          boxLength
        )
      }
    }
  }

}
