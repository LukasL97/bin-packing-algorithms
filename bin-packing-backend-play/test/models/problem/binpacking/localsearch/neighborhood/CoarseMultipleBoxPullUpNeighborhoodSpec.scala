package models.problem.binpacking.localsearch.neighborhood

import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.SimpleBinPackingSolution
import org.scalatest.MustMatchers
import org.scalatest.WordSpec

class CoarseMultipleBoxPullUpNeighborhoodSpec extends WordSpec with MustMatchers {

  private val boxLength = 20

  private val generator = new CoarseMultipleBoxPullUpNeighborhood[SimpleBinPackingSolution](boxLength)

  "CoarseMultipleBoxPullUpNeighborhood" should {
    "create neighboring solutions correctly" when {
      "given a solution that has one neighbor with pulled up rectangles" in {
        val solution = SimpleBinPackingSolution(
          Map(
            Rectangle(1, 20, 17) -> Placing(Box(1, boxLength), Coordinates(0, 0)),
            Rectangle(2, 9, 2) -> Placing(Box(2, boxLength), Coordinates(0, 0)),
            Rectangle(3, 9, 2) -> Placing(Box(2, boxLength), Coordinates(2, 0)),
            Rectangle(4, 9, 2) -> Placing(Box(2, boxLength), Coordinates(4, 0)),
          )
        )
        val neighborhood = generator.createCoarseMultipleBoxPullUpNeighborhood(solution).toSeq
        neighborhood.size mustEqual 1
        val newSolution = neighborhood.head
        newSolution.placement.count(_._2.box.id == 1) mustEqual 3
        newSolution.placement.count(_._2.box.id == 2) mustEqual 1
        newSolution.placement.filter(_._2.box.id == 1).map(_._2.coordinates).toSet mustEqual Set(
          Coordinates(0, 0),
          Coordinates(0, 18),
          Coordinates(10, 18)
        )
      }
    }
  }

}
