package models.problem.binpacking.localsearch.neighborhood

import metrics.Metrics
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.transformation.SquashingSupport
import models.problem.binpacking.solution.transformation.TopLeftFirstPlacingSupport
import models.problem.binpacking.utils.TopLeftFirstCoordinateOrdering

import scala.collection.View

class TopLeftFirstBoxPullUpNeighborhood[A <: TopLeftFirstPlacingSupport[A] with SquashingSupport[A]](
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
        solutionWithRectangleRemoved.placeTopLeftFirstInSpecificBox(rectangle, boxId - 1, maxOverlap)
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
    }
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
