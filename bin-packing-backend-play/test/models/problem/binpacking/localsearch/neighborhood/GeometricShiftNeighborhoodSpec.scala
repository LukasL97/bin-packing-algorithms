package models.problem.binpacking.localsearch.neighborhood

import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.SimpleBinPackingSolution
import models.problem.binpacking.solution.update.RectanglesChanged
import org.scalatest.MustMatchers
import org.scalatest.WordSpec

class GeometricShiftNeighborhoodSpec extends WordSpec with MustMatchers {

  private val boxLength = 5

  private val neighborhood = new GeometricShiftNeighborhood[SimpleBinPackingSolution](boxLength)

  "GeometricShiftNeighborhood" should {

    "create maximally shifted solutions correctly" when {

      "given a solution where no shift is possible" in {
        val solution = SimpleBinPackingSolution(
          Map(
            Rectangle(1, 1, 1) -> Placing(Box(1, boxLength), Coordinates(2, 0))
          )
        )
        neighborhood.createMaximallyShiftedSolutions(solution, Up).toSet mustEqual Set.empty
      }

      "given a solution where a shift to the box edge is possible" in {
        val solution = SimpleBinPackingSolution(
          Map(
            Rectangle(1, 1, 1) -> Placing(Box(1, boxLength), Coordinates(2, 3))
          )
        )
        neighborhood.createMaximallyShiftedSolutions(solution, Up).toSet mustEqual Set(
          SimpleBinPackingSolution(
            Map(
              Rectangle(1, 1, 1) -> Placing(Box(1, boxLength), Coordinates(2, 0))
            ),
            RectanglesChanged(Set(1))
          )
        )
      }

      "given a solution where a shift to the edge of another rectangle is possible" in {
        val solution = SimpleBinPackingSolution(
          Map(
            Rectangle(1, 5, 1) -> Placing(Box(1, boxLength), Coordinates(0, 0)),
            Rectangle(2, 5, 1) -> Placing(Box(1, boxLength), Coordinates(0, 4))
          )
        )
        neighborhood.createMaximallyShiftedSolutions(solution, Up).toSet mustEqual Set(
          SimpleBinPackingSolution(
            Map(
              Rectangle(1, 5, 1) -> Placing(Box(1, boxLength), Coordinates(0, 0)),
              Rectangle(2, 5, 1) -> Placing(Box(1, boxLength), Coordinates(0, 1))
            ),
            RectanglesChanged(Set(2))
          )
        )
      }
    }

    "create maximally shifted solutions in entire box correctly" when {

      "given a solution where no shift is possible" in {
        val solution = SimpleBinPackingSolution(
          Map(
            Rectangle(1, 1, 1) -> Placing(Box(1, boxLength), Coordinates(2, 0))
          )
        )
        neighborhood.createEntireBoxMaximallyShiftedSolutions(solution, Up).toSet mustEqual Set.empty
      }

      "given a solution in which multiple rectangle can be shifted" in {
        val solution = SimpleBinPackingSolution(
          Map(
            Rectangle(1, 1, 1) -> Placing(Box(1, boxLength), Coordinates(2, 0)),
            Rectangle(2, 2, 1) -> Placing(Box(1, boxLength), Coordinates(3, 2)),
            Rectangle(3, 1, 1) -> Placing(Box(1, boxLength), Coordinates(4, 4))
          )
        )
        neighborhood.createEntireBoxMaximallyShiftedSolutions(solution, Up).toSet mustEqual Set(
          SimpleBinPackingSolution(
            Map(
              Rectangle(1, 1, 1) -> Placing(Box(1, boxLength), Coordinates(2, 0)),
              Rectangle(2, 2, 1) -> Placing(Box(1, boxLength), Coordinates(3, 0)),
              Rectangle(3, 1, 1) -> Placing(Box(1, boxLength), Coordinates(4, 1))
            ),
            RectanglesChanged(Set(1, 2, 3))
          )
        )
      }

      "given a solution in which rectangles in different boxes can be shifted" in {
        val solution = SimpleBinPackingSolution(
          Map(
            Rectangle(1, 2, 2) -> Placing(Box(1, boxLength), Coordinates(3, 0)),
            Rectangle(2, 1, 1) -> Placing(Box(2, boxLength), Coordinates(2, 2))
          )
        )
        neighborhood.createEntireBoxMaximallyShiftedSolutions(solution, Left).toSet mustEqual Set(
          SimpleBinPackingSolution(
            Map(
              Rectangle(1, 2, 2) -> Placing(Box(1, boxLength), Coordinates(3, 0)),
              Rectangle(2, 1, 1) -> Placing(Box(2, boxLength), Coordinates(0, 2))
            ),
            RectanglesChanged(Set(2))
          ),
          SimpleBinPackingSolution(
            Map(
              Rectangle(1, 2, 2) -> Placing(Box(1, boxLength), Coordinates(0, 0)),
              Rectangle(2, 1, 1) -> Placing(Box(2, boxLength), Coordinates(2, 2))
            ),
            RectanglesChanged(Set(1))
          )
        )
      }

      "given a solution in which the bottom-most rectangle can't be shifted but others can" in {
        val solution = SimpleBinPackingSolution(
          Map(
            Rectangle(1, 2, 3) -> Placing(Box(1, boxLength), Coordinates(0, 0)),
            Rectangle(2, 2, 2) -> Placing(Box(1, boxLength), Coordinates(0, 3)),
            Rectangle(3, 1, 1) -> Placing(Box(1, boxLength), Coordinates(3, 2))
          )
        )
        neighborhood.createEntireBoxMaximallyShiftedSolutions(solution, Up).toSet mustEqual Set(
          SimpleBinPackingSolution(
            Map(
              Rectangle(1, 2, 3) -> Placing(Box(1, boxLength), Coordinates(0, 0)),
              Rectangle(2, 2, 2) -> Placing(Box(1, boxLength), Coordinates(0, 3)),
              Rectangle(3, 1, 1) -> Placing(Box(1, boxLength), Coordinates(3, 0))
            ),
            RectanglesChanged(Set(1, 2, 3))
          )
        )
      }
    }
  }

}
