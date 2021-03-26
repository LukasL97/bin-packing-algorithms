package models.problem.binpacking.solution.transformation

import models.problem.binpacking.BinPackingTopLeftFirstPlacing
import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle

import scala.collection.SortedSet

trait TopLeftFirstPlacingSupport[A <: TopLeftFirstPlacingSupport[A]]
    extends BinPackingSolution with BinPackingTopLeftFirstPlacing {
  def placeTopLeftFirst(rectangle: Rectangle): A

  protected def findRectanglePlacing(
    rectangle: Rectangle,
    candidateCoordinates: Option[Map[Int, SortedSet[Coordinates]]] = None
  ): (Rectangle, Placing) = {
    val placementsPerBox = getPlacementsPerBox
    val sortedPlacementsPerBox = placementsPerBox.toSeq.sortBy {
      case (boxId, _) => boxId
    }
    val maxBoxId = sortedPlacementsPerBox.lastOption.map(_._1).getOrElse(0)
    sortedPlacementsPerBox
      .foldLeft(Option.empty[(Rectangle, Placing)]) {
        case (foundPlacing, (boxId, placement)) =>
          foundPlacing.orElse(
            placeRectangleInBoxAtMostTopLeftPoint(
              rectangle,
              placement,
              considerRotation = true,
              candidateCoordinates = candidateCoordinates.map(_(boxId).toSeq)
            ).map {
              case (rectangle, coordinates) => rectangle -> Placing(Box(boxId, boxLength), coordinates)
            }
          )
      }
      .getOrElse(
        rectangle -> Placing(
          Box(maxBoxId + 1, boxLength),
          Coordinates(0, 0)
        )
      )
  }
}
