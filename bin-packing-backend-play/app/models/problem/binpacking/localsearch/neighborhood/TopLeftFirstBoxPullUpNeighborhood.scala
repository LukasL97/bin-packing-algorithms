package models.problem.binpacking.localsearch.neighborhood

import metrics.Metrics
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.transformation.SquashingSupport
import models.problem.binpacking.solution.transformation.TopLeftFirstPlacingSupport
import models.problem.binpacking.solution.update.RectanglesChanged
import models.problem.binpacking.solution.update.UpdateStoringSupport
import models.problem.binpacking.utils.TopLeftFirstCoordinateOrdering

import scala.collection.View

class TopLeftFirstBoxPullUpNeighborhood[A <: TopLeftFirstPlacingSupport[A] with SquashingSupport[A] with UpdateStoringSupport[A]](
  val boxLength: Int
) extends TopLeftFirstCoordinateOrdering with Metrics {

  private val entireBoxPullUpCombinedAreaThresholdFactor = 0.8

  def createSolutionsWithSingleBoxPullUp(solution: A, maxOverlap: Option[Double] = None): View[A] = {
    getReversedPlacementsPerBoxWithoutFirstBox(solution).view.flatMap {
      case (boxId, placement) =>
        pullUpRectanglesFromBox(solution, boxId, placement.toSeq.sortBy(_._2).reverse.map(_._1), maxOverlap)
    }
  }

  private def pullUpRectanglesFromBox(
    solution: A,
    boxId: Int,
    rectangles: Seq[Rectangle],
    maxOverlap: Option[Double]
  ): View[A] = {
    rectangles.view.flatMap { rectangle =>
      withTimer("single-box-pull-up-neighborhood") {
        val solutionWithRectangleRemoved = solution.removeRectangleFromBox(rectangle.id, boxId).squashed
        solutionWithRectangleRemoved
          .placeTopLeftFirstInSpecificBox(rectangle, boxId - 1, maxOverlap)
          .map(_.setUpdate(RectanglesChanged(Set(rectangle.id))))
      }
    }
  }

  def createSolutionsWithMaximalBoxPullUp(solution: A, maxOverlap: Option[Double] = None): View[A] = {
    getReversedPlacementsPerBoxWithoutFirstBox(solution).view.flatMap {
      case (boxId, placement) =>
        withTimer("maximal-box-pull-up-neighborhood") {
          pullUpRectanglesFromBoxUntilFull(solution, boxId, placement.toSeq.sortBy(_._2).reverse.map(_._1), maxOverlap)
        }
    }
  }

  private def pullUpRectanglesFromBoxUntilFull(
    solution: A,
    boxId: Int,
    rectangles: Seq[Rectangle],
    maxOverlap: Option[Double]
  ): Option[A] = {
    val (updatedSolution, changed, _, changedRectangleIds) = rectangles.foldLeft(solution, false, false, Seq.empty[Int]) {
      case ((updatedSolution, changed, finished, ids), rectangle) =>
        if (finished) {
          (updatedSolution, changed, finished, ids)
        } else {
          val solutionWithRectangleRemoved = updatedSolution.removeRectangleFromBox(rectangle.id, boxId)
          val solutionWithRectanglePulledUp =
            solutionWithRectangleRemoved.placeTopLeftFirstInSpecificBox(rectangle, boxId - 1, maxOverlap)
          solutionWithRectanglePulledUp match {
            case Some(solution) => (solution, true, false, ids.appended(rectangle.id))
            case None => (updatedSolution, changed, true, ids)
          }
        }
    }
    if (changed) {
      Option(updatedSolution.squashed.setUpdate(RectanglesChanged(changedRectangleIds.toSet)))
    } else {
      Option.empty[A]
    }
  }

  def createSolutionsWithEntireBoxPullUp(solution: A, maxOverlap: Option[Double] = None): View[A] = {
    getReversedPlacementsPerBoxWithoutFirstBox(solution).view.collect {
      case (boxId, placement)
          if belowEntireBoxPullUpCombinedAreaThreshold(
            getOverallRectangleArea(placement) + getOverallRectangleArea(solution.getPlacementInSingleBox(boxId - 1))
          ) =>
        withTimer("entire-box-pull-up-neighborhood") {
          pullUpAllRectanglesFromBox(solution, boxId, placement.keys.toSeq, maxOverlap)
        }
    }.flatten
  }

  private def pullUpAllRectanglesFromBox(
    solution: A,
    boxId: Int,
    rectangles: Seq[Rectangle],
    maxOverlap: Option[Double]
  ): Option[A] = {
    rectangles.foldLeft(Option(solution)) {
      case (Some(updatedSolution), rectangle) =>
        val solutionWithRectangleRemoved = updatedSolution.removeRectangleFromBox(rectangle.id, boxId).squashed
        solutionWithRectangleRemoved.placeTopLeftFirstInSpecificBox(rectangle, boxId - 1, maxOverlap)
      case (None, _) => None
    }.map(_.setUpdate(RectanglesChanged(rectangles.map(_.id).toSet)))
  }

  private def belowEntireBoxPullUpCombinedAreaThreshold(area: Int): Boolean = {
    area <= entireBoxPullUpCombinedAreaThresholdFactor * boxLength * boxLength
  }

  private def getReversedPlacementsPerBoxWithoutFirstBox(solution: A): Seq[(Int, Map[Rectangle, Coordinates])] = {
    withTimer("get-reversed-placements-per-box-without-first-box") {
      val sortedPlacementsPerBox = solution.getPlacementsPerBox.toSeq.sortBy {
        case (boxId, _) => boxId
      }.reverse
      sortedPlacementsPerBox.filterNot {
        case (boxId, _) => boxId == 1
      }
    }
  }

  private def getOverallRectangleArea(placement: Map[Rectangle, Coordinates]): Int = {
    placement.keys.toSeq.map {
      case Rectangle(_, width, height) => width * height
    }.sum
  }

}
