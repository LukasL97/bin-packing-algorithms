package models.problem.binpacking.localsearch.neighborhood

import metrics.Metrics
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.transformation.SquashingSupport
import models.problem.binpacking.solution.transformation.TopLeftFirstPlacingSupport
import models.problem.binpacking.utils.TopLeftFirstCoordinateOrdering

import scala.collection.View

class TopLeftFirstBoxMergeNeighborhood[A <: TopLeftFirstPlacingSupport[A] with SquashingSupport[A]](
  val boxLength: Int
) extends TopLeftFirstCoordinateOrdering with Metrics {

  val boxMergeCombinedAreaThresholdFactor = 0.8

  def createSolutionsWithMergedBoxes(solution: A): View[A] = {
    val reversedPlacementsPerBox = getReversedPlacementsPerBox(solution)
    val placementsPerBoxUntilThreshold = collectBoxesUntilAreaExceedsThreshold(reversedPlacementsPerBox)
    val placementsPerBoxPrefixes = getPrefixes(placementsPerBoxUntilThreshold).reverse.view
    placementsPerBoxPrefixes.flatMap { placementsPerBoxPrefix =>
      withTimer("box-merge-neighborhood") {
        mergeBoxes(solution, placementsPerBoxPrefix)
      }
    }
  }

  private def getOverallRectangleArea(placement: Map[Rectangle, Coordinates]): Int = {
    placement.keys.toSeq.map {
      case Rectangle(_, width, height) => width * height
    }.sum
  }

  private def getReversedPlacementsPerBox(solution: A): Seq[(Int, Map[Rectangle, Coordinates])] = {
    withTimer("get-reversed-placements-per-box") {
      solution.getPlacementsPerBox.toSeq.sortBy {
        case (boxId, _) => boxId
      }.reverse
    }
  }

  private def collectBoxesUntilAreaExceedsThreshold(
    placementsPerBox: Seq[(Int, Map[Rectangle, Coordinates])]
  ): Seq[(Int, Map[Rectangle, Coordinates])] = {
    var consideredPlacementsPerBox = Seq.empty[(Int, Map[Rectangle, Coordinates])]
    var overallRectangleAreaInConsideredBoxes = 0
    var index = 0
    val allowedOverallRectangleArea = boxMergeCombinedAreaThresholdFactor * boxLength * boxLength
    while (index < placementsPerBox.size && overallRectangleAreaInConsideredBoxes + getOverallRectangleArea(
             placementsPerBox(index)._2
           ) <= allowedOverallRectangleArea) {
      overallRectangleAreaInConsideredBoxes += getOverallRectangleArea(placementsPerBox(index)._2)
      consideredPlacementsPerBox = consideredPlacementsPerBox.appended(placementsPerBox(index))
      index += 1
    }
    consideredPlacementsPerBox
  }

  private def mergeBoxes(solution: A, placementsPerBox: Seq[(Int, Map[Rectangle, Coordinates])]): Option[A] = {
    val boxes = placementsPerBox.sortBy(_._1)
    val pulledBoxes = boxes.tail
    val targetBoxId = boxes.head._1
    pulledBoxes.foldLeft(Option(solution)) {
      case (Some(updatedSolution), (boxId, placement)) =>
        pullUpAllRectanglesFromBox(updatedSolution, boxId, targetBoxId, placement.keys.toSeq)
      case (None, _) => None
    }.map(_.squashed)
  }

  private def pullUpAllRectanglesFromBox(
    solution: A,
    sourceBoxId: Int,
    targetBoxId: Int,
    rectangles: Seq[Rectangle]
  ): Option[A] = {
    rectangles.foldLeft(Option(solution)) {
      case (Some(updatedSolution), rectangle) =>
        val solutionWithRectangleRemoved = updatedSolution.removeRectangleFromBox(rectangle.id, sourceBoxId)
        solutionWithRectangleRemoved.placeTopLeftFirstInSpecificBox(rectangle, targetBoxId)
      case (None, _) => None
    }
  }

  private def getPrefixes[B](seq: Seq[B]): Seq[Seq[B]] = {
    for (i <- 2 to seq.size) yield seq.slice(0, i)
  }

}
