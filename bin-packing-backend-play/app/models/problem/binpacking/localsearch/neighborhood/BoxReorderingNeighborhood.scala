package models.problem.binpacking.localsearch.neighborhood

import metrics.Metrics
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.transformation.BoxReorderingSupport

import scala.collection.View

class BoxReorderingNeighborhood[A <: BoxReorderingSupport[A]] extends Metrics {

  def reorderBoxesByFillGrade(solution: A): View[A] = {
    withTimer("reorder-boxes-by-fill-grade-neighborhood") {
      val reorderedBoxIds = solution.getPlacementsPerBox.toSeq.sortBy {
        case (_, placement) => getOverallRectangleArea(placement)
      }.reverse.map {
        case (boxId, _) => boxId
      }
      Seq(solution.reorderBoxes(reorderedBoxIds)).view
    }
  }

  private def getOverallRectangleArea(placement: Map[Rectangle, Coordinates]): Int = {
    placement.keys.toSeq.map {
      case Rectangle(_, width, height) => width * height
    }.sum
  }

}
