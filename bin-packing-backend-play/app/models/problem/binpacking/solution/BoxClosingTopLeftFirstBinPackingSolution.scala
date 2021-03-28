package models.problem.binpacking.solution

import models.problem.binpacking.solution.initialization.EmptySolutionInitializer
import models.problem.binpacking.solution.transformation.TopLeftFirstPlacingSupport

import scala.collection.SortedSet

object BoxClosingTopLeftFirstBinPackingSolution
    extends EmptySolutionInitializer[BoxClosingTopLeftFirstBinPackingSolution] {

  override def apply(boxLength: Int): BoxClosingTopLeftFirstBinPackingSolution =
    new BoxClosingTopLeftFirstBinPackingSolution(
      Map.empty[Rectangle, Placing],
      Map.empty[Int, SortedSet[Coordinates]],
      Seq.empty[Int],
      boxLength
    )
}

case class BoxClosingTopLeftFirstBinPackingSolution(
  override val placement: Map[Rectangle, Placing],
  override val topLeftCandidates: Map[Int, SortedSet[Coordinates]],
  override val closedBoxes: Seq[Int],
  override val boxLength: Int
) extends AbstractTopLeftFirstBinPackingSolution with ClosedBoxes
    with TopLeftFirstPlacingSupport[BoxClosingTopLeftFirstBinPackingSolution] {

  override def placeTopLeftFirst(rectangle: Rectangle): BoxClosingTopLeftFirstBinPackingSolution = {
    val (placedRectangle, placing) = findRectanglePlacing(rectangle, Option(topLeftCandidates), closedBoxes)
    val updatedPlacement = placement.updated(placedRectangle, placing)
    val updatedCandidates = updateCandidates(placedRectangle, placing)
    if (placing.box.id == 1 || topLeftCandidates.contains(placing.box.id)) {
      copy(
        placement = updatedPlacement,
        topLeftCandidates = updatedCandidates,
      )
    } else {
      val updatedClosedBoxes = closedBoxes.appended(placing.box.id - 1)
      val candidatesWithoutClosedBox = updatedCandidates.removed(placing.box.id - 1)
      copy(
        placement = updatedPlacement,
        topLeftCandidates = candidatesWithoutClosedBox,
        closedBoxes = updatedClosedBoxes
      )
    }
  }

  override def placeTopLeftFirstInSpecificBox(
    rectangle: Rectangle,
    boxId: Int
  ): Option[BoxClosingTopLeftFirstBinPackingSolution] = {
    if (closedBoxes.contains(boxId)) {
      Option.empty[BoxClosingTopLeftFirstBinPackingSolution]
    } else {
      findRectanglePlacingInSpecificBox(rectangle, boxId, topLeftCandidates.get(boxId)).map {
        case (placedRectangle, coordinates) =>
          val placing = Placing(Box(boxId, boxLength), coordinates)
          val updatedPlacement = placement.updated(placedRectangle, placing)
          val updatedCandidates = updateCandidates(placedRectangle, placing)
          copy(
            placement = updatedPlacement,
            topLeftCandidates = updatedCandidates
          )
      }
    }
  }

  override def removeRectangleFromBox(rectangleId: Int, boxId: Int): BoxClosingTopLeftFirstBinPackingSolution = {
    throw new NotImplementedError()
  }
}

trait ClosedBoxes {
  val closedBoxes: Seq[Int]
}
