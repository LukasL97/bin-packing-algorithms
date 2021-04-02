package models.problem.binpacking.solution

import models.problem.binpacking.solution.initialization.EmptySolutionInitializer
import models.problem.binpacking.solution.transformation.TopLeftFirstPlacingSupport
import models.problem.binpacking.solution.update.StartSolution
import models.problem.binpacking.solution.update.Update

import scala.collection.SortedSet

object BoxClosingTopLeftFirstBinPackingSolution
    extends EmptySolutionInitializer[BoxClosingTopLeftFirstBinPackingSolution] {

  override def apply(boxLength: Int): BoxClosingTopLeftFirstBinPackingSolution = {
    new BoxClosingTopLeftFirstBinPackingSolution(
      Map.empty[Rectangle, Placing],
      Map.empty[Int, SortedSet[Coordinates]],
      Seq.empty[Int],
      Seq.empty[Rectangle],
      boxLength,
      StartSolution()
    )
  }

  def apply(rectangles: Seq[Rectangle], boxLength: Int): BoxClosingTopLeftFirstBinPackingSolution = {
    rectangles.foldLeft(apply(boxLength)) {
      case (solution, rectangle) => solution.placeTopLeftFirst(rectangle)
    }
  }
}

case class BoxClosingTopLeftFirstBinPackingSolution(
  override val placement: Map[Rectangle, Placing],
  override val topLeftCandidates: Map[Int, SortedSet[Coordinates]],
  override val closedBoxes: Seq[Int],
  rectangles: Seq[Rectangle],
  override val boxLength: Int,
  override val update: Update
) extends AbstractTopLeftFirstBinPackingSolution with ClosedBoxes
    with TopLeftFirstPlacingSupport[BoxClosingTopLeftFirstBinPackingSolution] {

  override def placeTopLeftFirst(
    rectangle: Rectangle,
    maxOverlap: Option[Double] = None
  ): BoxClosingTopLeftFirstBinPackingSolution = {
    require(maxOverlap.isEmpty)
    val (placedRectangle, placing) = findRectanglePlacing(rectangle, Option(topLeftCandidates), closedBoxes)
    val updatedPlacement = placement.updated(placedRectangle, placing)
    val updatedCandidates = updateCandidates(placedRectangle, placing)
    val updatedRectangles = rectangles.appended(placedRectangle)
    if (placing.box.id == 1 || topLeftCandidates.contains(placing.box.id)) {
      copy(
        placement = updatedPlacement,
        topLeftCandidates = updatedCandidates,
        rectangles = updatedRectangles
      )
    } else {
      val updatedClosedBoxes = closedBoxes.appended(placing.box.id - 1)
      copy(
        placement = updatedPlacement,
        topLeftCandidates = updatedCandidates,
        closedBoxes = updatedClosedBoxes,
        rectangles = updatedRectangles
      )
    }
  }

  override def placeTopLeftFirstInSpecificBox(
    rectangle: Rectangle,
    boxId: Int,
    maxOverlap: Option[Double] = None
  ): Option[BoxClosingTopLeftFirstBinPackingSolution] = {
    require(maxOverlap.isEmpty)
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
            topLeftCandidates = updatedCandidates,
            rectangles = rectangles.appended(placedRectangle)
          )
      }
    }
  }

  override def removeRectangleFromBox(rectangleId: Int, boxId: Int): BoxClosingTopLeftFirstBinPackingSolution = {
    throw new NotImplementedError()
  }

  def prefixWithOpenLastBox(lastBoxId: Int): BoxClosingTopLeftFirstBinPackingSolution = {
    copy(
      placement = placement.filter(_._2.box.id <= lastBoxId),
      topLeftCandidates = topLeftCandidates.filter(_._1 <= lastBoxId),
      closedBoxes = closedBoxes.filter(_ < lastBoxId),
      rectangles = rectangles.filter(placement(_).box.id <= lastBoxId)
    )
  }
}

trait ClosedBoxes {
  val closedBoxes: Seq[Int]
}
