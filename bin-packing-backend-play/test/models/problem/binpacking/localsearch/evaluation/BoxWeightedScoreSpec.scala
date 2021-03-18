package models.problem.binpacking.localsearch.evaluation

import models.algorithm.OneDimensionalScore
import org.scalatest.MustMatchers
import org.scalatest.WordSpec

class BoxWeightedScoreSpec extends WordSpec with MustMatchers {

  "BoxWeightedScore" should {
    "compare box score sequences correctly" when {
      "given two box score sequences with different length" in {
        BoxWeightedScore(
          Seq(
            OneDimensionalScore(1),
            OneDimensionalScore(2),
            OneDimensionalScore(3),
          )
        ).compareTo(
          BoxWeightedScore(
            Seq(
              OneDimensionalScore(4),
              OneDimensionalScore(5)
            )
          )
        ) mustBe >(0)
      }
      "given two box score sequences with equal length and different score in last box" in {
        BoxWeightedScore(
          Seq(
            OneDimensionalScore(4),
            OneDimensionalScore(8),
            OneDimensionalScore(3),
          )
        ).compareTo(
          BoxWeightedScore(
            Seq(
              OneDimensionalScore(1),
              OneDimensionalScore(2),
              OneDimensionalScore(4)
            )
          )
        ) mustBe <(0)
      }
      "given two box score sequences with equal length and different score in an earlier position" in {
        BoxWeightedScore(
          Seq(
            OneDimensionalScore(4),
            OneDimensionalScore(8),
            OneDimensionalScore(3),
          )
        ).compareTo(
          BoxWeightedScore(
            Seq(
              OneDimensionalScore(1),
              OneDimensionalScore(2),
              OneDimensionalScore(3)
            )
          )
        ) mustBe >(0)
      }
      "given two identical box score sequences" in {
        BoxWeightedScore(
          Seq(
            OneDimensionalScore(4),
            OneDimensionalScore(8),
            OneDimensionalScore(3),
          )
        ).compareTo(
          BoxWeightedScore(
            Seq(
              OneDimensionalScore(4),
              OneDimensionalScore(8),
              OneDimensionalScore(3)
            )
          )
        ) mustEqual 0
      }
    }
  }

}
