package models.problem.rectangles

import models.algorithm.SolutionHandler
import org.scalatest.{MustMatchers, WordSpec}

class RectanglesPlacementSolutionHandlerSpec extends WordSpec with MustMatchers {

  "RectanglesPlacementSolutionHandler" should {
    "validate feasibility correctly" when {

      val solutionHandler = new RectanglesPlacementSolutionHandler {
        override def getNeighborhood(solution: RectanglesPlacementSolution) = Set.empty[RectanglesPlacementSolution]
        override def evaluate(solution: RectanglesPlacementSolution): BigDecimal = 0.0
        override def createArbitraryFeasibleSolution(): RectanglesPlacementSolution = RectanglesPlacementSolution(Map.empty)
      }

      val box1 = Box(0, 10, 10)
      val box2 = Box(1, 10, 10)

      "given a non-overlapping in-box placement" in {
        val solution = RectanglesPlacementSolution(Map(
          Rectangle(0, 5, 5) -> (box1, (0, 0)),
          Rectangle(1, 5, 7) -> (box1, (5, 2)),
          Rectangle(2, 3, 3) -> (box1, (1, 6)),
          Rectangle(3, 2, 8) -> (box2, (1, 1)),
          Rectangle(4, 5, 5) -> (box2, (5, 5))
        ))

        solutionHandler.isFeasible(solution) mustEqual true
      }

      "given an overlapping placement" in {
        val solution = RectanglesPlacementSolution(Map(
          Rectangle(0, 5, 5) -> (box1, (0, 0)),
          Rectangle(1, 5, 7) -> (box1, (5, 2)),
          Rectangle(2, 3, 3) -> (box1, (1, 6)),
          Rectangle(3, 2, 8) -> (box2, (1, 1)),
          Rectangle(4, 5, 5) -> (box2, (2, 5))
        ))

        solutionHandler.isFeasible(solution) mustBe false
      }

      "given an out-of-box placement" in {
        val solution = RectanglesPlacementSolution(Map(
          Rectangle(0, 5, 5) -> (box1, (0, 0)),
          Rectangle(1, 5, 5) -> (box2, (6, 5))
        ))

        solutionHandler.isFeasible(solution) mustBe false
      }
    }
  }

}
