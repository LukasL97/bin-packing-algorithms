package models.problem.binpacking.localsearch.neighborhood

import metrics.Metrics
import models.problem.binpacking.BinPackingTopLeftFirstPlacing
import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.transformation.RectanglePlacingUpdateSupport
import models.problem.binpacking.solution.transformation.SquashingSupport

import scala.collection.View

class CoarseMultipleBoxPullUpNeighborhood[A <: RectanglePlacingUpdateSupport[A] with SquashingSupport[A]](
  override val boxLength: Int
) extends BinPackingTopLeftFirstPlacing with Metrics {

  val granularity = 10

  override lazy val horizontalStepSize: Int = boxLength / granularity
  override lazy val verticalStepSize: Int = boxLength / granularity

  def createCoarseMultipleBoxPullUpNeighborhood(solution: A): View[A] = {
    val placementsPerBox = solution.getPlacementsPerBox
    val placementsPerBoxInDescendingOrder = placementsPerBox.toSeq.sortBy {
      case (boxId, _) => boxId
    }.reverse
    placementsPerBoxInDescendingOrder.filter {
      case (boxId, _) => boxId > 1
    }.view.map {
      case (boxId, placement) =>
        withTimer("coarse-multiple-box-pull-up-neighborhood") {
          val updatedPlacings = placeRectanglesInBoxAtMostTopLeftPointUntilFull(
            placement.keys.toSeq,
            placementsPerBox(boxId - 1)
          ).map {
            case (rectangle, coordinates) => rectangle -> Placing(Box(boxId - 1, boxLength), coordinates)
          }.toMap
          solution.updated(updatedPlacings).squashed
        }
    }
  }

  private def placeRectanglesInBoxAtMostTopLeftPointUntilFull(
    rectangles: Seq[Rectangle],
    placement: Map[Rectangle, Coordinates],
    placedRectangles: Seq[(Rectangle, Coordinates)] = Seq.empty
  ): Seq[(Rectangle, Coordinates)] = {
    rectangles.headOption.flatMap { rectangle =>
      placeRectangleInBoxAtMostTopLeftPoint(rectangle, placement, considerRotation = true).map {
        case (rectangle, coordinates) =>
          placeRectanglesInBoxAtMostTopLeftPointUntilFull(
            rectangles.tail,
            placement.updated(rectangle, coordinates),
            placedRectangles.appended((rectangle, coordinates))
          )
      }
    }.getOrElse(placedRectangles)
  }

}
