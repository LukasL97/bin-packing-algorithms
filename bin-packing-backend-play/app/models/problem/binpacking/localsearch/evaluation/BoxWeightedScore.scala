package models.problem.binpacking.localsearch.evaluation

import metrics.Metrics
import models.algorithm.Score

import scala.annotation.tailrec

case class BoxWeightedScore(
  boxScores: Seq[Score]
) extends Score with Metrics {

  override def compare(that: Score): Int = withTimer("box-weighted-score-compare") {
    val other = that.asInstanceOf[BoxWeightedScore]
    boxScores.size.compareTo(other.boxScores.size) match {
      case 0 => compareBoxScores(boxScores, other.boxScores)
      case result => result
    }
  }

  @tailrec
  private def compareBoxScores(thisScores: Seq[Score], otherScores: Seq[Score]): Int = {
    (thisScores.lastOption, otherScores.lastOption) match {
      case (None, None) => 0
      case (Some(thisScore), Some(otherScore)) =>
        thisScore.compareTo(otherScore) match {
          case 0 => compareBoxScores(thisScores.dropRight(1), otherScores.dropRight(1))
          case result => result
        }
      case _ => throw new RuntimeException("Box scores with different length should have been caught before")
    }
  }
}
