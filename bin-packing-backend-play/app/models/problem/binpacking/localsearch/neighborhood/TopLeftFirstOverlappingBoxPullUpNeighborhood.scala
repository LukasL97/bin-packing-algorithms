package models.problem.binpacking.localsearch.neighborhood

import metrics.Metrics
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.OverlappingTopLeftFirstBinPackingSolution
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.utils.TopLeftFirstCoordinateOrdering

import scala.collection.View

class TopLeftFirstOverlappingBoxPullUpNeighborhood(
  val boxLength: Int
) extends TopLeftFirstCoordinateOrdering with Metrics {

  def createSolutionsWithSingleBoxPullUp(
    solution: OverlappingTopLeftFirstBinPackingSolution,
    maxOverlap: Double
  ): View[OverlappingTopLeftFirstBinPackingSolution] = {
    getReversedPlacementsPerBoxWithoutFirstBox(solution).view.flatMap {
      case (boxId, placement) =>
        pullUpRectanglesFromBox(solution, boxId, placement.toSeq.sortBy(_._2).reverse.map(_._1), maxOverlap)
    }
  }

  private def getReversedPlacementsPerBoxWithoutFirstBox(
    solution: OverlappingTopLeftFirstBinPackingSolution
  ): Seq[(Int, Map[Rectangle, Coordinates])] = {
    withTimer("get-reversed-placements-per-box-without-first-box") {
      val sortedPlacementsPerBox = solution.getPlacementsPerBox.toSeq.sortBy {
        case (boxId, _) => boxId
      }.reverse
      sortedPlacementsPerBox.filterNot {
        case (boxId, _) => boxId == 1
      }
    }
  }

  private def pullUpRectanglesFromBox(
    solution: OverlappingTopLeftFirstBinPackingSolution,
    boxId: Int,
    rectangles: Seq[Rectangle],
    maxOverlap: Double
  ): View[OverlappingTopLeftFirstBinPackingSolution] = {
    rectangles.view.flatMap { rectangle =>
      withTimer("single-box-pull-up-neighborhood") {
        val solutionWithRectangleRemoved = solution.removeRectangleFromBox(rectangle.id, boxId).squashed
        solutionWithRectangleRemoved.placeTopLeftFirstInSpecificBox(rectangle, boxId - 1, maxOverlap)
      }
    }
  }

  def createSolutionsWithMaximalBoxPullUp(
    solution: OverlappingTopLeftFirstBinPackingSolution,
    maxOverlap: Double
  ): View[OverlappingTopLeftFirstBinPackingSolution] = {
    getReversedPlacementsPerBoxWithoutFirstBox(solution).view.flatMap {
      case (boxId, placement) =>
        withTimer("maximal-box-pull-up-neighborhood") {
          pullUpRectanglesFromBoxUntilFull(solution, boxId, placement.toSeq.sortBy(_._2).reverse.map(_._1), maxOverlap)
        }
    }
  }

  private def pullUpRectanglesFromBoxUntilFull(
    solution: OverlappingTopLeftFirstBinPackingSolution,
    boxId: Int,
    rectangles: Seq[Rectangle],
    maxOverlap: Double
  ): Option[OverlappingTopLeftFirstBinPackingSolution] = {
    val (updatedSolution, changed, _) = rectangles.foldLeft(solution, false, false) {
      case ((updatedSolution, changed, finished), rectangle) =>
        if (finished) {
          (updatedSolution, changed, finished)
        } else {
          val solutionWithRectangleRemoved = updatedSolution.removeRectangleFromBox(rectangle.id, boxId)
          val solutionWithRectanglePulledUp =
            solutionWithRectangleRemoved.placeTopLeftFirstInSpecificBox(rectangle, boxId - 1, maxOverlap)
          solutionWithRectanglePulledUp match {
            case Some(solution) => (solution, true, false)
            case None => (updatedSolution, changed, true)
          }
        }
    }
    if (changed) {
      Option(updatedSolution.squashed)
    } else {
      Option.empty[OverlappingTopLeftFirstBinPackingSolution]
    }
  }

}
