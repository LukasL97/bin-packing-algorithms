package models.problem.binpacking.localsearch.neighborhood

import metrics.Metrics
import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Rectangle

import scala.collection.View

class BoxReorderingNeighborhood extends Metrics {

  def reorderBoxesByFillGrade(solution: BinPackingSolution): View[BinPackingSolution] = {
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
