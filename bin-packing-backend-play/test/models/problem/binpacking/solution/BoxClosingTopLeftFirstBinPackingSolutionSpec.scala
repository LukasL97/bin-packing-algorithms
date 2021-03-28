package models.problem.binpacking.solution

import org.scalatest.MustMatchers
import org.scalatest.WordSpec

class BoxClosingTopLeftFirstBinPackingSolutionSpec extends WordSpec with MustMatchers {

  val boxLength = 10

  "BoxClosingTopLeftFirstBinPackingSolution" should {
    "not place rectangles into closed boxes even if they would fit" when {
      "given rectangles so that the third one would fit into the already closed first box" in {
        val solution = BoxClosingTopLeftFirstBinPackingSolution(boxLength)
        val rectangles = Seq(
          Rectangle(1, 10, 9),
          Rectangle(2, 10, 2),
          Rectangle(3, 1, 1)
        )
        val finalSolution = rectangles.foldLeft(solution) {
          case (updatedSolution, rectangle) => updatedSolution.placeTopLeftFirst(rectangle)
        }
        finalSolution.closedBoxes mustEqual Seq(1)
        finalSolution.placement mustEqual Map(
          Rectangle(1, 10, 9) -> Placing(Box(1, boxLength), Coordinates(0, 0)),
          Rectangle(2, 10, 2) -> Placing(Box(2, boxLength), Coordinates(0, 0)),
          Rectangle(3, 1, 1) -> Placing(Box(2, boxLength), Coordinates(0, 2))
        )
      }
    }
  }

}
