package models.problem.binpacking.localsearch.neighborhood

import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.SimpleBinPackingSolution
import models.problem.binpacking.solution.update.BoxOrderChanged
import org.scalatest.MustMatchers
import org.scalatest.WordSpec

class BoxReorderingNeighborhoodSpec extends WordSpec with MustMatchers {

  val generator = new BoxReorderingNeighborhood[SimpleBinPackingSolution]

  "BoxReorderingNeighborhood" should {
    "reorder boxes in descending order according to their fill grade" when {
      "given a solution" in {
        val solution = SimpleBinPackingSolution(
          Map(
            Rectangle(1, 1, 1) -> Placing(Box(1, 10), Coordinates(0, 0)),
            Rectangle(2, 6, 3) -> Placing(Box(2, 10), Coordinates(1, 0)),
            Rectangle(3, 1, 3) -> Placing(Box(3, 10), Coordinates(0, 0)),
            Rectangle(4, 1, 3) -> Placing(Box(3, 10), Coordinates(5, 0)),
          )
        )
        generator.reorderBoxesByFillGrade(solution).head mustEqual SimpleBinPackingSolution(
          Map(
            Rectangle(1, 1, 1) -> Placing(Box(3, 10), Coordinates(0, 0)),
            Rectangle(2, 6, 3) -> Placing(Box(1, 10), Coordinates(1, 0)),
            Rectangle(3, 1, 3) -> Placing(Box(2, 10), Coordinates(0, 0)),
            Rectangle(4, 1, 3) -> Placing(Box(2, 10), Coordinates(5, 0)),
          ),
          BoxOrderChanged()
        )
      }
    }
  }

}
