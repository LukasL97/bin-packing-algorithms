package models.problem.binpacking.localsearch.neighborhood

import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.SimpleBinPackingSolution
import org.scalatest.MustMatchers
import org.scalatest.WordSpec

class BoxMergeNeighborhoodSpec extends WordSpec with MustMatchers {

  private val boxLength = 10

  private val rectangles = Seq(
    Rectangle(1, 5, 5),
    Rectangle(2, 4, 4),
    Rectangle(3, 5, 4),
    Rectangle(4, 2, 3),
    Rectangle(5, 2, 1),
    Rectangle(6, 2, 1),
    Rectangle(7, 2, 1)
  )

  private val generator = new BoxMergeNeighborhood(rectangles.toSet, boxLength)

  "BoxMergeNeighborhood" should {
    "merge boxes correctly" when {
      "two of three boxes can be merged in a solution" in {
        val solution = SimpleBinPackingSolution(
          Map(
            rectangles.head -> Placing(Box(1, boxLength), Coordinates(0, 0)),
            rectangles(1) -> Placing(Box(1, boxLength), Coordinates(5, 0)),
            rectangles(2) -> Placing(Box(2, boxLength), Coordinates(0, 0)),
            rectangles(3) -> Placing(Box(2, boxLength), Coordinates(5, 0)),
            rectangles(4) -> Placing(Box(2, boxLength), Coordinates(7, 7)),
            rectangles(5) -> Placing(Box(3, boxLength), Coordinates(0, 0)),
            rectangles(6) -> Placing(Box(3, boxLength), Coordinates(7, 7))
          )
        )
        val newSolution = generator.createBoxMergeNeighborhood(solution).head
        Seq(
          rectangles.head,
          rectangles(1),
          rectangles(5),
          rectangles(6)
        ).foreach {
          rectangle => newSolution.placement(rectangle).box.id mustEqual 1
        }
        Seq(
          rectangles(2),
          rectangles(3),
          rectangles(4)
        ).foreach {
          rectangle => newSolution.placement(rectangle).box.id mustEqual 2
        }
      }
    }
  }

}
