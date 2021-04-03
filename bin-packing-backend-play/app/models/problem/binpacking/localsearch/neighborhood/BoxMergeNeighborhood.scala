package models.problem.binpacking.localsearch.neighborhood

import metrics.Metrics
import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.transformation.RectanglePlacingUpdateSupport
import models.problem.binpacking.solution.transformation.SquashingSupport
import models.problem.binpacking.solution.update.RectanglesChanged
import models.problem.binpacking.solution.update.UpdateStoringSupport

import scala.annotation.tailrec
import scala.collection.View

class BoxMergeNeighborhood[A <: RectanglePlacingUpdateSupport[A] with SquashingSupport[A] with UpdateStoringSupport[A]](
  val rectangles: Set[Rectangle],
  val boxLength: Int
) extends Metrics {

  private lazy val maxRectangleWidth = rectangles.map(_.width).max
  private lazy val maxRectangleHeight = rectangles.map(_.height).max

  def createBoxMergeNeighborhood(solution: A): View[A] = {
    withTimer("box-merge-neighborhood") {
      val sortedRectangleGroups = solution.placement.groupBy {
        case (_, placing) => placing.box.id
      }.map {
        case (boxId, placement) => boxId -> placement.keys
      }.toSeq.sortBy {
        case (_, rectangles) => rectangles.size
      }
      val coordinates = getCoordinates
      val mergeableRectangleGroups = collectRectanglesUntilBoxFull(sortedRectangleGroups, coordinates.size)
      if (mergeableRectangleGroups.length <= 1) {
        Seq.empty[A].view
      } else {
        val (boxIds, rectangleGroups) = mergeableRectangleGroups.unzip
        val mergedBoxId = boxIds.min
        val mergedBoxRectangles = rectangleGroups.flatten
        assert(mergedBoxRectangles.size <= coordinates.size)
        val mergedBoxPlacement = mergedBoxRectangles
          .zip(coordinates)
          .map {
            case (rectangle, coordinates) => rectangle -> Placing(Box(mergedBoxId, boxLength), coordinates)
          }
          .toMap
        val mergedBoxSolution = solution
          .updated(mergedBoxPlacement)
          .squashed
          .setUpdate(RectanglesChanged(mergedBoxRectangles.map(_.id).toSet))
        Seq(mergedBoxSolution).view
      }
    }
  }

  private def getCoordinates: Seq[Coordinates] =
    for {
      x <- 0 to (boxLength - maxRectangleWidth) by maxRectangleWidth
      y <- 0 to (boxLength - maxRectangleHeight) by maxRectangleHeight
    } yield Coordinates(x, y)

  @tailrec
  private def collectRectanglesUntilBoxFull(
    rectangleGroups: Seq[(Int, Iterable[Rectangle])],
    maxRectangles: Int,
    collectedRectangles: Seq[(Int, Iterable[Rectangle])] = Seq.empty
  ): Seq[(Int, Iterable[Rectangle])] = {
    val numCollectedRectangles = collectedRectangles.map {
      case (boxId, rectangles) => rectangles.size
    }.sum
    rectangleGroups match {
      case Nil => collectedRectangles
      case (_, rectangles) :: _ if rectangles.size + numCollectedRectangles > maxRectangles => collectedRectangles
      case group :: rest => collectRectanglesUntilBoxFull(rest, maxRectangles, collectedRectangles.appended(group))
    }
  }

}
