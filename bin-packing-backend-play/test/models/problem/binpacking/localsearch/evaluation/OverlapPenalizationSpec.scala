package models.problem.binpacking.localsearch.evaluation

import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.SimpleBinPackingSolution
import org.scalatest.MustMatchers
import org.scalatest.WordSpec

class OverlapPenalizationSpec extends WordSpec with MustMatchers {

  private val overlapPenalization = new OverlapPenalization {}

  "OverlapPenalization" should {

    "calculate overlap correctly" when {
      "given a rectangle fully contained in another rectangle" in {
        overlapPenalization.overlap(
          (Rectangle(1, 5, 5), Coordinates(0, 0)),
          (Rectangle(2 ,1, 1), Coordinates(1, 2))
        ) mustEqual (1.0 / 25)
      }
      "given two overlapping but not contained rectangles" in {
        overlapPenalization.overlap(
          (Rectangle(1, 5, 5), Coordinates(0, 0)),
          (Rectangle(2, 3, 5), Coordinates(1, 2))
        ) mustEqual (9.0 / 25)
      }
      "given two disjunctive rectangles" in {
        overlapPenalization.overlap(
          (Rectangle(1, 5, 5), Coordinates(0, 0)),
          (Rectangle(2, 5, 5), Coordinates(5, 2))
        ) mustEqual 0.0
      }
    }

    "calculate escape distance correctly" when {
      "given a rectangle fully contained in another rectangle" in {
        overlapPenalization.escapeDistance(
          (Rectangle(1, 5, 5), Coordinates(0, 0)),
          (Rectangle(2 ,1, 1), Coordinates(1, 2))
        ) mustEqual 2
      }
      "given two overlapping but not contained rectangles" in {
        overlapPenalization.escapeDistance(
          (Rectangle(1, 5, 5), Coordinates(0, 0)),
          (Rectangle(2, 3, 5), Coordinates(1, 2))
        ) mustEqual 3
      }
    }

    "penalize solution correctly" when {
      val solution = SimpleBinPackingSolution(Map(
        Rectangle(1, 4, 2) -> Placing(Box(1, 10), Coordinates(0, 0)),
        Rectangle(2, 3, 2) -> Placing(Box(1, 10), Coordinates(2, 0)),
        Rectangle(3, 5, 5) -> Placing(Box(2, 10), Coordinates(0, 0)),
        Rectangle(4, 1, 1) -> Placing(Box(2, 10), Coordinates(0, 0)),
        Rectangle(5, 2, 2) -> Placing(Box(2, 10), Coordinates(8, 8))
      ))

      "no overlap is allowed" in {
        overlapPenalization.penalizeOverlap(solution, 0.0) mustEqual BigDecimal(3)
      }
      "enough overlap for the overlapping pair in box 2 is allowed" in {
        overlapPenalization.penalizeOverlap(solution, 1.0 / 25) mustEqual BigDecimal(2)
      }
      "enough overlap for both overlapping pairs is allowed" in {
        overlapPenalization.penalizeOverlap(solution, 4.0 / 8) mustEqual BigDecimal(0)
      }
    }
  }

}
