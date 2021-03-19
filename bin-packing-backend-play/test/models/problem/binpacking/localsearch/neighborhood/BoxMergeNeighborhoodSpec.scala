package models.problem.binpacking.localsearch.neighborhood

import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.SimpleBinPackingSolution
import org.scalatest.MustMatchers
import org.scalatest.WordSpec

class BoxMergeNeighborhoodSpec extends WordSpec with MustMatchers {

  private val boxLength_ = 10

  private val rectangles_ = Seq(
    Rectangle(1, 5, 5),
    Rectangle(2, 4, 4),
    Rectangle(3, 5, 4),
    Rectangle(4, 2, 3),
    Rectangle(5, 2, 1),
    Rectangle(6, 2, 1),
    Rectangle(7, 2, 1)
  )

  private val generator = new BoxMergeNeighborhood {
    override val rectangles: Set[Rectangle] = rectangles_.toSet
    override val boxLength: Int = boxLength_
  }

  "BoxMergeNeighborhood" should {
    "merge boxes correctly" when {
      "two of three boxes can be merged in a solution" in {
        val solution = SimpleBinPackingSolution(
          Map(
            rectangles_.head -> Placing(Box(1, boxLength_), Coordinates(0, 0)),
            rectangles_(1) -> Placing(Box(1, boxLength_), Coordinates(5, 0)),
            rectangles_(2) -> Placing(Box(2, boxLength_), Coordinates(0, 0)),
            rectangles_(3) -> Placing(Box(2, boxLength_), Coordinates(5, 0)),
            rectangles_(4) -> Placing(Box(2, boxLength_), Coordinates(7, 7)),
            rectangles_(5) -> Placing(Box(3, boxLength_), Coordinates(0, 0)),
            rectangles_(6) -> Placing(Box(3, boxLength_), Coordinates(7, 7))
          )
        )
        val newSolution = generator.createBoxMergeNeighborhood(solution).head
        Seq(
          rectangles_.head,
          rectangles_(1),
          rectangles_(5),
          rectangles_(6)
        ).foreach {
          rectangle => newSolution.placement(rectangle).box.id mustEqual 1
        }
        Seq(
          rectangles_(2),
          rectangles_(3),
          rectangles_(4)
        ).foreach {
          rectangle => newSolution.placement(rectangle).box.id mustEqual 2
        }
      }
    }
  }

}
