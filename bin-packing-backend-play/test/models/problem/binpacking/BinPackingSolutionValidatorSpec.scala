package models.problem.binpacking

import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.SimpleBinPackingSolution
import org.scalatest.MustMatchers
import org.scalatest.WordSpec

class BinPackingSolutionValidatorSpec extends WordSpec with MustMatchers {

  private val solutionValidator = new BinPackingSolutionValidator {}

  "BinPackingSolutionValidator" should {
    "validate feasibility correctly" when {

      val box1 = Box(0, 10)
      val box2 = Box(1, 10)

      "given a non-overlapping in-box placement" in {
        val solution = SimpleBinPackingSolution(
          Map(
            Rectangle(0, 5, 5) -> Placing(box1, Coordinates(0, 0)),
            Rectangle(1, 5, 7) -> Placing(box1, Coordinates(5, 2)),
            Rectangle(2, 3, 3) -> Placing(box1, Coordinates(1, 6)),
            Rectangle(3, 2, 8) -> Placing(box2, Coordinates(1, 1)),
            Rectangle(4, 5, 5) -> Placing(box2, Coordinates(5, 5))
          )
        )
        solutionValidator.isFeasible(solution) mustEqual true
      }

      "given an overlapping placement" in {
        val solution = SimpleBinPackingSolution(
          Map(
            Rectangle(0, 5, 5) -> Placing(box1, Coordinates(0, 0)),
            Rectangle(1, 5, 7) -> Placing(box1, Coordinates(5, 2)),
            Rectangle(2, 3, 3) -> Placing(box1, Coordinates(1, 6)),
            Rectangle(3, 2, 8) -> Placing(box2, Coordinates(1, 1)),
            Rectangle(4, 5, 5) -> Placing(box2, Coordinates(2, 5))
          )
        )
        solutionValidator.isFeasible(solution) mustBe false
      }

      "given an out-of-box placement" in {
        val solution = SimpleBinPackingSolution(
          Map(
            Rectangle(0, 5, 5) -> Placing(box1, Coordinates(0, 0)),
            Rectangle(1, 5, 5) -> Placing(box2, Coordinates(6, 5))
          )
        )
        solutionValidator.isFeasible(solution) mustBe false
      }
    }
  }

}
