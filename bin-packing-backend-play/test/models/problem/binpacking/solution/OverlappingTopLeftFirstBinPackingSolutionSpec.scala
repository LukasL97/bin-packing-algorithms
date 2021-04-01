package models.problem.binpacking.solution

import models.problem.binpacking.utils.TopLeftFirstCoordinateOrdering
import org.scalatest.MustMatchers
import org.scalatest.WordSpec

import scala.collection.SortedSet

class OverlappingTopLeftFirstBinPackingSolutionSpec
    extends WordSpec with MustMatchers with TopLeftFirstCoordinateOrdering {

  private val boxLength = 10

  "OverlappingTopLeftFirstBinPackingSolution" must {

    "place rectangles top left first" when {

      "allowed overlap is 0" in {
        val solution = OverlappingTopLeftFirstBinPackingSolution(
          Map(
            Rectangle(1, 1, 1) -> Placing(Box(1, boxLength), Coordinates(0, 0)),
            Rectangle(2, 2, 1) -> Placing(Box(1, boxLength), Coordinates(0, 1))
          ),
          Map(1 -> SortedSet(Coordinates(1, 0), Coordinates(2, 0), Coordinates(0, 2))),
          Map(1 -> Set.empty[Overlapping]),
          boxLength
        )
        val rectangle = Rectangle(3, 2, 2)
        solution.placeTopLeftFirst(rectangle, Option(0)) mustEqual OverlappingTopLeftFirstBinPackingSolution(
          Map(
            Rectangle(1, 1, 1) -> Placing(Box(1, boxLength), Coordinates(0, 0)),
            Rectangle(2, 2, 1) -> Placing(Box(1, boxLength), Coordinates(0, 1)),
            rectangle -> Placing(Box(1, boxLength), Coordinates(0, 2))
          ),
          Map(
            1 -> SortedSet(
              Coordinates(1, 0),
              Coordinates(2, 0),
              Coordinates(1, 4),
              Coordinates(0, 4),
              Coordinates(2, 2)
            )
          ),
          Map(1 -> Set.empty[Overlapping]),
          boxLength
        )
      }

      "allowed overlap is sufficient for the rectangle to be placed with overlap" in {
        val solution = OverlappingTopLeftFirstBinPackingSolution(
          Map(
            Rectangle(1, 1, 1) -> Placing(Box(1, boxLength), Coordinates(0, 0)),
            Rectangle(2, 2, 1) -> Placing(Box(1, boxLength), Coordinates(0, 1))
          ),
          Map(1 -> SortedSet(Coordinates(1, 0), Coordinates(2, 0), Coordinates(0, 2))),
          Map(1 -> Set.empty[Overlapping]),
          boxLength
        )
        val rectangle = Rectangle(3, 2, 2)
        solution.placeTopLeftFirst(rectangle, Option(0.3)) mustEqual OverlappingTopLeftFirstBinPackingSolution(
          Map(
            Rectangle(1, 1, 1) -> Placing(Box(1, boxLength), Coordinates(0, 0)),
            Rectangle(2, 2, 1) -> Placing(Box(1, boxLength), Coordinates(0, 1)),
            rectangle -> Placing(Box(1, boxLength), Coordinates(1, 0))
          ),
          Map(1 -> SortedSet(Coordinates(0, 2), Coordinates(1, 2), Coordinates(2, 2), Coordinates(3, 0))),
          Map(1 -> Set(Overlapping(rectangle, Coordinates(1, 0), Rectangle(2, 2, 1), Coordinates(0, 1), 0.25))),
          boxLength
        )
      }
    }

    "place a rectangle top left first in a specific box" when {

      "it fits only because of the allowed overlap" in {
        val solution = OverlappingTopLeftFirstBinPackingSolution(
          Map(
            Rectangle(1, 8, 10) -> Placing(Box(1, boxLength), Coordinates(2, 0)),
          ),
          Map(1 -> SortedSet(Coordinates(0, 0))),
          Map(
            1 -> Set.empty[Overlapping]
          ),
          boxLength
        )
        val rectangle = Rectangle(2, 5, 5)
        solution.placeTopLeftFirstInSpecificBox(rectangle, 1, Option(0.3)) mustEqual Option(
          OverlappingTopLeftFirstBinPackingSolution(
            Map(
              Rectangle(1, 8, 10) -> Placing(Box(1, boxLength), Coordinates(2, 0)),
              rectangle -> Placing(Box(1, boxLength), Coordinates(0, 0))
            ),
            Map(1 -> SortedSet(Coordinates(0, 5))),
            Map(
              1 -> Set(Overlapping(rectangle, Coordinates(0, 0), Rectangle(1, 8, 10), Coordinates(2, 0), 15.0 / 80.0))
            ),
            boxLength
          )
        )
      }

      "it does not fit because of the allowed overlap" in {
        val solution = OverlappingTopLeftFirstBinPackingSolution(
          Map(
            Rectangle(1, 8, 10) -> Placing(Box(1, boxLength), Coordinates(2, 0)),
          ),
          Map(1 -> SortedSet(Coordinates(0, 0))),
          Map(
            1 -> Set.empty[Overlapping]
          ),
          boxLength
        )
        val rectangle = Rectangle(2, 5, 5)
        solution.placeTopLeftFirstInSpecificBox(rectangle, 1, Option(0.1)) must be(None)
      }
    }

    "remove a rectangle from a box" when {
      "an overlapping has to be removed with the removed rectangle" in {
        val solution = OverlappingTopLeftFirstBinPackingSolution(
          Map(
            Rectangle(1, 1, 1) -> Placing(Box(1, boxLength), Coordinates(0, 0)),
            Rectangle(2, 2, 1) -> Placing(Box(1, boxLength), Coordinates(0, 1)),
            Rectangle(3, 2, 2) -> Placing(Box(1, boxLength), Coordinates(1, 0))
          ),
          Map(1 -> SortedSet(Coordinates(0, 2), Coordinates(1, 2), Coordinates(2, 2), Coordinates(3, 0))),
          Map(
            1 -> Set(Overlapping(Rectangle(3, 2, 2), Coordinates(1, 0), Rectangle(2, 2, 1), Coordinates(0, 1), 0.25))
          ),
          boxLength
        )
        solution.removeRectangleFromBox(3, 1) mustEqual OverlappingTopLeftFirstBinPackingSolution(
          Map(
            Rectangle(1, 1, 1) -> Placing(Box(1, boxLength), Coordinates(0, 0)),
            Rectangle(2, 2, 1) -> Placing(Box(1, boxLength), Coordinates(0, 1)),
          ),
          Map(1 -> SortedSet(Coordinates(1, 0), Coordinates(2, 0), Coordinates(0, 2))),
          Map(1 -> Set.empty[Overlapping]),
          boxLength
        )
      }
    }

    "initialize from given rectangles" when {
      "any overlap is allowed" in {
        val rectangles = Seq(
          Rectangle(1, 1, 1),
          Rectangle(2, 3, 1),
          Rectangle(3, 3, 3),
          Rectangle(4, 2, 2),
          Rectangle(5, 8, 8)
        )
        val solution = OverlappingTopLeftFirstBinPackingSolution.apply(rectangles, boxLength, 1.0)
        solution.placement mustEqual Map(
          Rectangle(1, 1, 1) -> Placing(Box(1, boxLength), Coordinates(0, 0)),
          Rectangle(2, 3, 1) -> Placing(Box(1, boxLength), Coordinates(0, 1)),
          Rectangle(3, 3, 3) -> Placing(Box(1, boxLength), Coordinates(1, 0)),
          Rectangle(4, 2, 2) -> Placing(Box(1, boxLength), Coordinates(0, 2)),
          Rectangle(5, 8, 8) -> Placing(Box(2, boxLength), Coordinates(0, 0))
        )
        solution.overlappings mustEqual Map(
          1 -> Set(
            Overlapping(Rectangle(3, 3, 3), Coordinates(1, 0), Rectangle(2, 3, 1), Coordinates(0, 1), 2.0 / 9),
            Overlapping(Rectangle(4, 2, 2), Coordinates(0, 2), Rectangle(3, 3, 3), Coordinates(1, 0), 1.0 / 9)
          ),
          2 -> Set.empty[Overlapping]
        )
      }
    }
  }

}
