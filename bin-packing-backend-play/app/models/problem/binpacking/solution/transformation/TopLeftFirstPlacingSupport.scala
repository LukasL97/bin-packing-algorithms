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

  def placeTopLeftFirstInSpecificBox(rectangle: Rectangle, boxId: Int): Option[A]

  def removeRectangleFromBox(rectangleId: Int, boxId: Int): A

  protected def findRectanglePlacing(
    rectangle: Rectangle,
    candidateCoordinates: Option[Map[Int, SortedSet[Coordinates]]] = None,
    ignoreBoxes: Seq[Int] = Seq.empty
  ): (Rectangle, Placing) = {
    val placementsPerBox = getPlacementsPerBox
    val sortedPlacementsPerBox = placementsPerBox.toSeq.sortBy {
      case (boxId, _) => boxId
    }
    val maxBoxId = sortedPlacementsPerBox.lastOption.map(_._1).getOrElse(0)
    sortedPlacementsPerBox
      .foldLeft(Option.empty[(Rectangle, Placing)]) {
        case (foundPlacing, (boxId, placement)) =>
          foundPlacing.orElse {
            if (ignoreBoxes.contains(boxId)) {
              Option.empty[(Rectangle, Placing)]
            } else {
              placeRectangleInBoxAtMostTopLeftPoint(
                rectangle,
                placement,
                considerRotation = true,
                candidateCoordinates = candidateCoordinates.map(_(boxId).toSeq)
              ).map {
                case (rectangle, coordinates) => rectangle -> Placing(Box(boxId, boxLength), coordinates)
              }
            }
          }
      }
      .getOrElse(
        rectangle -> Placing(
          Box(maxBoxId + 1, boxLength),
          Coordinates(0, 0)
        )
      )
  }

  protected def findRectanglePlacingInSpecificBox(
    rectangle: Rectangle,
    boxId: Int,
    candidateCoordinates: Option[SortedSet[Coordinates]] = None
  ): Option[(Rectangle, Coordinates)] = {
    val placementInBox = getPlacementInSingleBox(boxId)
    placeRectangleInBoxAtMostTopLeftPoint(
      rectangle,
      placementInBox,
      considerRotation = true,
      candidateCoordinates = candidateCoordinates.map(_.toSeq)
    )
  }
}
