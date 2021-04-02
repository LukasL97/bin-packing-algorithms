package models.problem.binpacking.solution

import models.problem.binpacking.solution.initialization.EmptySolutionInitializer
import models.problem.binpacking.solution.initialization.OneRectanglePerBoxSolutionInitializer
import models.problem.binpacking.solution.transformation.BoxReorderingSupport
import models.problem.binpacking.solution.transformation.SquashingSupport
import models.problem.binpacking.solution.transformation.TopLeftFirstPlacingSupport
import models.problem.binpacking.solution.update.StartSolution
import models.problem.binpacking.solution.update.Update

import scala.collection.SortedSet

object TopLeftFirstBinPackingSolution
    extends EmptySolutionInitializer[TopLeftFirstBinPackingSolution]
    with OneRectanglePerBoxSolutionInitializer[TopLeftFirstBinPackingSolution] {

  override def apply(boxLength: Int): TopLeftFirstBinPackingSolution = new TopLeftFirstBinPackingSolution(
    Map.empty[Rectangle, Placing],
    Map.empty[Int, SortedSet[Coordinates]],
    boxLength,
    StartSolution()
  )

  override def apply(rectangles: Seq[Rectangle], boxLength: Int): TopLeftFirstBinPackingSolution = {
    val emptySolution = TopLeftFirstBinPackingSolution(boxLength)
    rectangles.zipWithIndex.foldLeft(emptySolution) {
      case (solution, (rectangle, index)) =>
        solution
          .placeTopLeftFirstInSpecificBox(rectangle, index + 1)
          .getOrElse(
            throw new RuntimeException(s"Failed initializing ${getClass.getSimpleName} with one rectangle per box")
          )
    }
  }
}

case class TopLeftFirstBinPackingSolution(
  override val placement: Map[Rectangle, Placing],
  override val topLeftCandidates: Map[Int, SortedSet[Coordinates]],
  override val boxLength: Int,
  override val update: Update
) extends AbstractTopLeftFirstBinPackingSolution with TopLeftFirstPlacingSupport[TopLeftFirstBinPackingSolution]
    with BoxReorderingSupport[TopLeftFirstBinPackingSolution] with SquashingSupport[TopLeftFirstBinPackingSolution] {

  override def asSimpleSolution: SimpleBinPackingSolution = SimpleBinPackingSolution(placement)

  override def removeRectangleFromBox(rectangleId: Int, boxId: Int): TopLeftFirstBinPackingSolution = {
    val (rectangle, coordinates) = getPlacementInSingleBox(boxId).find {
      case (rectangle, _) => rectangle.id == rectangleId
    }.getOrElse(
      throw new RuntimeException(s"Rectangle with id $rectangleId was not in box with id $boxId")
    )
    val updatedPlacement = placement.removed(rectangle)
    val updatedBoxPlacement = updatedPlacement.collect {
      case (rectangle, Placing(box, coordinates)) if box.id == boxId => rectangle -> coordinates
    }
    if (updatedBoxPlacement.isEmpty) {
      copy(
        placement = updatedPlacement,
        topLeftCandidates = topLeftCandidates.removed(boxId)
      )
    } else {
      val updatedBoxCandidates =
        updateCandidatesOnRectangleRemoval(rectangle, coordinates, topLeftCandidates(boxId), updatedBoxPlacement)
      copy(
        placement = updatedPlacement,
        topLeftCandidates = topLeftCandidates.updated(boxId, updatedBoxCandidates)
      )
    }
  }

  override def placeTopLeftFirst(
    rectangle: Rectangle,
    maxOverlap: Option[Double] = None
  ): TopLeftFirstBinPackingSolution = {
    require(maxOverlap.isEmpty)
    val (placedRectangle, placing) = findRectanglePlacing(rectangle, Option(topLeftCandidates))
    val updatedPlacement = placement.updated(placedRectangle, placing)
    val updatedCandidates = updateCandidates(placedRectangle, placing)
    copy(
      placement = updatedPlacement,
      topLeftCandidates = updatedCandidates,
      boxLength = boxLength
    )
  }

  override def placeTopLeftFirstInSpecificBox(
    rectangle: Rectangle,
    boxId: Int,
    maxOverlap: Option[Double] = None
  ): Option[TopLeftFirstBinPackingSolution] = {
    require(maxOverlap.isEmpty)
    findRectanglePlacingInSpecificBox(rectangle, boxId, topLeftCandidates.get(boxId)).map {
      case (placedRectangle, coordinates) =>
        val placing = Placing(Box(boxId, boxLength), coordinates)
        val updatedPlacement = placement.updated(placedRectangle, placing)
        val updatedCandidates = updateCandidates(placedRectangle, placing)
        copy(
          placement = updatedPlacement,
          topLeftCandidates = updatedCandidates,
          boxLength = boxLength
        )
    }
  }

  override def reorderBoxes(boxIdOrder: Seq[Int]): TopLeftFirstBinPackingSolution = {
    val newPlacement = reorderPlacement(boxIdOrder)
    val boxIdMapping = boxIdOrder.zip(1 to boxIdOrder.size).toMap
    val newCandidates = topLeftCandidates.map {
      case (boxId, candidates) => boxIdMapping(boxId) -> candidates
    }
    copy(
      placement = newPlacement,
      topLeftCandidates = newCandidates
    )
  }

  override def squashed: TopLeftFirstBinPackingSolution = {
    val boxIdSquashMapping = getBoxIdSquashMapping
    val updatedPlacement = squashPlacement(boxIdSquashMapping)
    val updatedCandidates = topLeftCandidates.map {
      case (boxId, candidates) => boxIdSquashMapping(boxId) -> candidates
    }
    copy(
      placement = updatedPlacement,
      topLeftCandidates = updatedCandidates
    )
  }
}
