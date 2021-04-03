package models.problem.binpacking.localsearch.neighborhood

import metrics.Metrics
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.transformation.BoxReorderingSupport
import models.problem.binpacking.solution.update.BoxOrderChanged
import models.problem.binpacking.solution.update.UpdateStoringSupport

import scala.collection.View

class BoxReorderingNeighborhood[A <: BoxReorderingSupport[A] with UpdateStoringSupport[A]] extends Metrics {

  def reorderBoxesByFillGrade(solution: A): View[A] = {
    withTimer("reorder-boxes-by-fill-grade-neighborhood") {
      val reorderedBoxIds = solution.getPlacementsPerBox.toSeq.sorted.map {
        case (boxId, _) => boxId
      }
      if (reorderedBoxIds == (1 to reorderedBoxIds.size)) {
        View.empty[A]
      } else {
        Seq(solution.reorderBoxes(reorderedBoxIds).setUpdate(BoxOrderChanged())).view
      }
    }
  }

  private implicit val fillGradeOrdering: Ordering[(Int, Map[Rectangle, Coordinates])] =
    (x: (Int, Map[Rectangle, Coordinates]), y: (Int, Map[Rectangle, Coordinates])) => {
      val fillGradeComparison = getOverallRectangleArea(y._2) - getOverallRectangleArea(x._2)
      if (fillGradeComparison == 0) {
        x._1 - y._1
      } else {
        fillGradeComparison
      }
    }

  private def getOverallRectangleArea(placement: Map[Rectangle, Coordinates]): Int = {
    placement.keys.toSeq.map {
      case Rectangle(_, width, height) => width * height
    }.sum
  }

}
