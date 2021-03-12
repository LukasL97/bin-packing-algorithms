package models.problem.binpacking.localsearch.neighborhood

import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.SimpleBinPackingSolution
import org.scalatest.MustMatchers
import org.scalatest.WordSpec

class GeometricShiftNeighborhoodSpec extends WordSpec with MustMatchers {

  private val neighborhood = new GeometricShiftNeighborhood {}

  "GeometricShiftNeighborhood" should {
    "create maximally shifted solutions correctly" when {

      "given a solution where no shift is possible" in {
        val solution = SimpleBinPackingSolution(Map(
          Rectangle(1, 1, 1) -> Placing(Box(1, 5), Coordinates(2, 0))
        ))
        neighborhood.createMaximallyShiftedSolutions(solution, Up) mustEqual Set.empty
      }

      "given a solution where a shift to the box edge is possible" in {
        val solution = SimpleBinPackingSolution(Map(
          Rectangle(1, 1, 1) -> Placing(Box(1, 5), Coordinates(2, 3))
        ))
        neighborhood.createMaximallyShiftedSolutions(solution, Up) mustEqual Set(
          SimpleBinPackingSolution(Map(
            Rectangle(1, 1, 1) -> Placing(Box(1, 5), Coordinates(2, 0))
          ))
        )
      }

      "given a solution where a shift to the edge of another rectangle is possible" in {
        val solution = SimpleBinPackingSolution(Map(
          Rectangle(1, 5, 1) -> Placing(Box(1, 5), Coordinates(0, 0)),
          Rectangle(2, 5, 1) -> Placing(Box(1, 5), Coordinates(0, 4))
        ))
        neighborhood.createMaximallyShiftedSolutions(solution, Up) mustEqual Set(
          SimpleBinPackingSolution(Map(
            Rectangle(1, 5, 1) -> Placing(Box(1, 5), Coordinates(0, 0)),
            Rectangle(2, 5, 1) -> Placing(Box(1, 5), Coordinates(0, 1))
          ))
        )
      }
    }
  }

}
