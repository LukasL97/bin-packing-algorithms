package models.problem.binpacking.localsearch.neighborhood

import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.SimpleBinPackingSolution
import org.scalatest.MustMatchers
import org.scalatest.WordSpec

class GeometricShiftNeighborhoodSpec extends WordSpec with MustMatchers {

  private val boxLength_ = 5

  private val neighborhood = new GeometricShiftNeighborhood {
    override val boxLength: Int = boxLength_
  }

  "GeometricShiftNeighborhood" should {

    "create maximally shifted solutions correctly" when {

      "given a solution where no shift is possible" in {
        val solution = SimpleBinPackingSolution(
          Map(
            Rectangle(1, 1, 1) -> Placing(Box(1, boxLength_), Coordinates(2, 0))
          )
        )
        neighborhood.createMaximallyShiftedSolutions(solution, Up).toSet mustEqual Set.empty
      }

      "given a solution where a shift to the box edge is possible" in {
        val solution = SimpleBinPackingSolution(
          Map(
            Rectangle(1, 1, 1) -> Placing(Box(1, boxLength_), Coordinates(2, 3))
          )
        )
        neighborhood.createMaximallyShiftedSolutions(solution, Up).toSet mustEqual Set(
          SimpleBinPackingSolution(
            Map(
              Rectangle(1, 1, 1) -> Placing(Box(1, boxLength_), Coordinates(2, 0))
            )
          )
        )
      }

      "given a solution where a shift to the edge of another rectangle is possible" in {
        val solution = SimpleBinPackingSolution(
          Map(
            Rectangle(1, 5, 1) -> Placing(Box(1, boxLength_), Coordinates(0, 0)),
            Rectangle(2, 5, 1) -> Placing(Box(1, boxLength_), Coordinates(0, 4))
          )
        )
        neighborhood.createMaximallyShiftedSolutions(solution, Up).toSet mustEqual Set(
          SimpleBinPackingSolution(
            Map(
              Rectangle(1, 5, 1) -> Placing(Box(1, boxLength_), Coordinates(0, 0)),
              Rectangle(2, 5, 1) -> Placing(Box(1, boxLength_), Coordinates(0, 1))
            )
          )
        )
      }
    }

    "create maximally shifted solutions in entire box correctly" when {

      "given a solution where no shift is possible" in {
        val solution = SimpleBinPackingSolution(
          Map(
            Rectangle(1, 1, 1) -> Placing(Box(1, boxLength_), Coordinates(2, 0))
          )
        )
        neighborhood.createEntireBoxMaximallyShiftedSolutions(solution, Up).toSet mustEqual Set.empty
      }

      "given a solution in which multiple rectangle can be shifted" in {
        val solution = SimpleBinPackingSolution(
          Map(
            Rectangle(1, 1, 1) -> Placing(Box(1, boxLength_), Coordinates(2, 0)),
            Rectangle(2, 2, 1) -> Placing(Box(1, boxLength_), Coordinates(3, 2)),
            Rectangle(3, 1, 1) -> Placing(Box(1, boxLength_), Coordinates(4, 4))
          )
        )
        neighborhood.createEntireBoxMaximallyShiftedSolutions(solution, Up).toSet mustEqual Set(
          SimpleBinPackingSolution(
            Map(
              Rectangle(1, 1, 1) -> Placing(Box(1, boxLength_), Coordinates(2, 0)),
              Rectangle(2, 2, 1) -> Placing(Box(1, boxLength_), Coordinates(3, 0)),
              Rectangle(3, 1, 1) -> Placing(Box(1, boxLength_), Coordinates(4, 1))
            )
          )
        )
      }

      "given a solution in which rectangles in different boxes can be shifted" in {
        val solution = SimpleBinPackingSolution(
          Map(
            Rectangle(1, 2, 2) -> Placing(Box(1, boxLength_), Coordinates(3, 0)),
            Rectangle(2, 1, 1) -> Placing(Box(2, boxLength_), Coordinates(2, 2))
          )
        )
        neighborhood.createEntireBoxMaximallyShiftedSolutions(solution, Left).toSet mustEqual Set(
          SimpleBinPackingSolution(
            Map(
              Rectangle(1, 2, 2) -> Placing(Box(1, boxLength_), Coordinates(3, 0)),
              Rectangle(2, 1, 1) -> Placing(Box(2, boxLength_), Coordinates(0, 2))
            )
          ),
          SimpleBinPackingSolution(
            Map(
              Rectangle(1, 2, 2) -> Placing(Box(1, boxLength_), Coordinates(0, 0)),
              Rectangle(2, 1, 1) -> Placing(Box(2, boxLength_), Coordinates(2, 2))
            )
          )
        )
      }

      "given a solution in which the bottom-most rectangle can't be shifted but others can" in {
        val solution = SimpleBinPackingSolution(
          Map(
            Rectangle(1, 2, 3) -> Placing(Box(1, boxLength_), Coordinates(0, 0)),
            Rectangle(2, 2, 2) -> Placing(Box(1, boxLength_), Coordinates(0, 3)),
            Rectangle(3, 1, 1) -> Placing(Box(1, boxLength_), Coordinates(3, 2))
          )
        )
        neighborhood.createEntireBoxMaximallyShiftedSolutions(solution, Up).toSet mustEqual Set(
          SimpleBinPackingSolution(
            Map(
              Rectangle(1, 2, 3) -> Placing(Box(1, boxLength_), Coordinates(0, 0)),
              Rectangle(2, 2, 2) -> Placing(Box(1, boxLength_), Coordinates(0, 3)),
              Rectangle(3, 1, 1) -> Placing(Box(1, boxLength_), Coordinates(3, 0))
            )
          )
        )
      }
    }
  }

}
