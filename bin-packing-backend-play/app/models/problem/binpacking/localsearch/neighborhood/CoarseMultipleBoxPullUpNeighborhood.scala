package models.problem.binpacking.localsearch.neighborhood

import models.problem.binpacking.BinPackingTopLeftFirstPlacing
import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle

import scala.collection.View

trait CoarseMultipleBoxPullUpNeighborhood extends BinPackingTopLeftFirstPlacing {

  val granularity = 10

  override lazy val horizontalStepSize: Int = boxLength / granularity
  override lazy val verticalStepSize: Int = boxLength / granularity

  def createCoarseMultipleBoxPullUpNeighborhood(solution: BinPackingSolution): View[BinPackingSolution] = {
    val placementsPerBox = solution.getPlacementsPerBox
    placementsPerBox.filter {
      case (boxId, _) => boxId > 1
    }.view.map {
      case (boxId, placement) =>
        val updatedPlacings = placeRectanglesInBoxAtMostTopLeftPointUntilFull(
          placement.keys.toSeq,
          placementsPerBox(boxId - 1)
        ).map {
          case (rectangle, coordinates) => rectangle -> Placing(Box(boxId - 1, boxLength), coordinates)
        }.toMap
        solution.updated(updatedPlacings).squashed
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
