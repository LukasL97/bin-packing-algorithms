package models.problem.binpacking.localsearch.initialization

import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import org.scalatest.MustMatchers
import org.scalatest.WordSpec

class GeometricStartSolutionSpec extends WordSpec with MustMatchers {

  private val boxLength_ = 10

  private val generator = new GeometricStartSolution {
    override val rectangles: Set[Rectangle] = Set(
      Rectangle(1, 4, 5),
      Rectangle(2, 5, 5),
      Rectangle(3, 1, 2),
      Rectangle(4, 4, 2),
      Rectangle(5, 1, 3)
    )
    override val boxLength: Int = boxLength_
  }

  "GeometricStartSolution" should {
    "create a trivially feasible start solution correctly" in {
      generator.triviallyFeasibleStartSolution.placement.values.toSet mustEqual Set(
        Placing(Box(1, boxLength_), Coordinates(0, 0)),
        Placing(Box(1, boxLength_), Coordinates(5, 0)),
        Placing(Box(1, boxLength_), Coordinates(0, 5)),
        Placing(Box(1, boxLength_), Coordinates(5, 5)),
        Placing(Box(2, boxLength_), Coordinates(0, 0))
      )
    }
  }

}
