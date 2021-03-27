package models.problem.binpacking.solution

import models.problem.binpacking.BinPackingTopLeftFirstPlacing
import models.problem.binpacking.solution.initialization.EmptySolutionInitializer
import models.problem.binpacking.solution.initialization.OneRectanglePerBoxSolutionInitializer
import models.problem.binpacking.solution.transformation.BoxReorderingSupport
import models.problem.binpacking.solution.transformation.SquashingSupport
import models.problem.binpacking.solution.transformation.TopLeftFirstPlacingSupport

import scala.collection.SortedSet

object TopLeftFirstBinPackingSolution
    extends EmptySolutionInitializer[TopLeftFirstBinPackingSolution]
    with OneRectanglePerBoxSolutionInitializer[TopLeftFirstBinPackingSolution] {

  override def apply(boxLength: Int): TopLeftFirstBinPackingSolution = new TopLeftFirstBinPackingSolution(
    Map.empty[Rectangle, Placing],
    Map.empty[Int, SortedSet[Coordinates]],
    boxLength
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
  override val boxLength: Int
) extends BinPackingSolution with TopLeftCandidates with BinPackingTopLeftFirstPlacing
    with TopLeftFirstPlacingSupport[TopLeftFirstBinPackingSolution]
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

  private def updateCandidatesOnRectangleRemoval(
    rectangle: Rectangle,
    coordinates: Coordinates,
    boxCandidates: SortedSet[Coordinates],
    updatedBoxPlacement: Map[Rectangle, Coordinates]
  ): SortedSet[Coordinates] = {
    val rectangleRightEdge =
      VerticalEdge(coordinates.x + rectangle.width, coordinates.y, coordinates.y + rectangle.height)
    val rectangleBottomEdge =
      HorizontalEdge(coordinates.y + rectangle.height, coordinates.x, coordinates.x + rectangle.width)
    val candidatesInRectangleRightEdge = boxCandidates.filter(rectangleRightEdge.containsNotBottom)
    val unliftedCandidatesInRectangleRightEdge = candidatesInRectangleRightEdge
      .map(c => Coordinates(coordinates.x, c.y))
      .filter(isInSomeRightEdge(_, updatedBoxPlacement))
    val candidatesInRectangleBottomEdge = boxCandidates.filter(rectangleBottomEdge.containsNotRight)
    val unliftedCandidatesInRectangleBottomEdge = candidatesInRectangleBottomEdge
      .map(c => Coordinates(c.x, coordinates.y))
      .filter(isInSomeBottomEdge(_, updatedBoxPlacement))
    boxCandidates.diff(candidatesInRectangleRightEdge ++ candidatesInRectangleBottomEdge) ++
      unliftedCandidatesInRectangleRightEdge ++
      unliftedCandidatesInRectangleBottomEdge ++
      Seq(coordinates)
  }

  override def placeTopLeftFirst(rectangle: Rectangle): TopLeftFirstBinPackingSolution = {
    val (placedRectangle, placing) = findRectanglePlacing(rectangle, Option(topLeftCandidates))
    val updatedPlacement = placement.updated(placedRectangle, placing)
    val updatedCandidates = updateCandidates(placedRectangle, placing)
    TopLeftFirstBinPackingSolution(
      updatedPlacement,
      updatedCandidates,
      boxLength
    )
  }

  override def placeTopLeftFirstInSpecificBox(
    rectangle: Rectangle,
    boxId: Int
  ): Option[TopLeftFirstBinPackingSolution] = {
    findRectanglePlacingInSpecificBox(rectangle, boxId, topLeftCandidates.get(boxId)).map {
      case (placedRectangle, coordinates) =>
        val placing = Placing(Box(boxId, boxLength), coordinates)
        val updatedPlacement = placement.updated(placedRectangle, placing)
        val updatedCandidates = updateCandidates(placedRectangle, placing)
        TopLeftFirstBinPackingSolution(
          updatedPlacement,
          updatedCandidates,
          boxLength
        )
    }
  }

  private def updateCandidates(
    rectangle: Rectangle,
    placing: Placing
  ): Map[Int, SortedSet[Coordinates]] = {
    val placementsPerBox = getPlacementsPerBox
    val maxBoxId = placementsPerBox.keys.maxOption.getOrElse(0)
    val rectangleTopRight = Coordinates(placing.coordinates.x + rectangle.width, placing.coordinates.y)
    val rectangleBottomLeft = Coordinates(placing.coordinates.x, placing.coordinates.y + rectangle.height)
    if (placing.box.id > maxBoxId) {
      val newBoxCandidates = SortedSet(rectangleBottomLeft, rectangleTopRight)
      topLeftCandidates.updated(placing.box.id, newBoxCandidates)
    } else {
      topLeftCandidates.updated(
        placing.box.id,
        updateCandidatesInPreviouslyExistingBox(
          placing.coordinates,
          rectangleTopRight,
          rectangleBottomLeft,
          topLeftCandidates(placing.box.id),
          placementsPerBox(placing.box.id)
        )
      )
    }
  }

  private def updateCandidatesInPreviouslyExistingBox(
    rectangleTopLeft: Coordinates,
    rectangleTopRight: Coordinates,
    rectangleBottomLeft: Coordinates,
    candidates: SortedSet[Coordinates],
    oldPlacement: Map[Rectangle, Coordinates]
  ): SortedSet[Coordinates] = {
    val candidatesWithShadowedCandidatesLifted = liftCandidatesShadowedByNewRectangleLeftEdge(
      rectangleTopLeft,
      rectangleBottomLeft,
      rectangleTopRight.x,
      liftCandidatesShadowedByNewRectangleTopEdge(
        rectangleTopLeft,
        rectangleTopRight,
        rectangleBottomLeft.y,
        candidates,
        oldPlacement
      ),
      oldPlacement
    )
    val candidatesWithCoveredCoordinatesDropped = dropCandidatesCoveredByNewRectangle(
      rectangleTopLeft,
      rectangleTopRight,
      rectangleBottomLeft,
      candidatesWithShadowedCandidatesLifted
    )
    val newCandidates = Seq(
      getNewCandidateFromRectangleTopRight(rectangleTopRight, oldPlacement),
      getNewCandidateFromRectangleBottomLeft(rectangleBottomLeft, oldPlacement)
    ).flatten
    candidatesWithCoveredCoordinatesDropped ++ newCandidates
  }

  private def liftCandidatesShadowedByNewRectangleLeftEdge(
    rectangleTopLeft: Coordinates,
    rectangleBottomLeft: Coordinates,
    rectangleRightX: Int,
    candidates: SortedSet[Coordinates],
    placement: Map[Rectangle, Coordinates]
  ): SortedSet[Coordinates] = {
    val rectangleLeftEdge = VerticalEdge(rectangleTopLeft.x, rectangleTopLeft.y, rectangleBottomLeft.y)
    val liftedCandidates = candidates.collect {
      case Coordinates(x, y)
          if x <= rectangleLeftEdge.x && rectangleLeftEdge.top <= y && y < rectangleLeftEdge.bottom =>
        Coordinates(rectangleRightX, y)
    }
    candidates ++ liftedCandidates.filterNot(isInSomeLeftEdge(_, placement))
  }

  private def liftCandidatesShadowedByNewRectangleTopEdge(
    rectangleTopLeft: Coordinates,
    rectangleTopRight: Coordinates,
    rectangleBottomY: Int,
    candidates: SortedSet[Coordinates],
    placement: Map[Rectangle, Coordinates]
  ): SortedSet[Coordinates] = {
    val rectangleTopEdge = HorizontalEdge(rectangleTopLeft.y, rectangleTopLeft.x, rectangleTopRight.x)
    val liftedCandidates = candidates.collect {
      case Coordinates(x, y) if y <= rectangleTopEdge.y && rectangleTopEdge.left <= x && x < rectangleTopEdge.right =>
        Coordinates(x, rectangleBottomY)
    }
    candidates ++ liftedCandidates.filterNot(isInSomeTopEdge(_, placement))
  }

  private def dropCandidatesCoveredByNewRectangle(
    rectangleTopLeft: Coordinates,
    rectangleTopRight: Coordinates,
    rectangleBottomLeft: Coordinates,
    candidates: SortedSet[Coordinates]
  ): SortedSet[Coordinates] = {
    val rectangleTopEdge = HorizontalEdge(rectangleTopLeft.y, rectangleTopLeft.x, rectangleTopRight.x)
    val rectangleLeftEdge = VerticalEdge(rectangleTopLeft.x, rectangleTopLeft.y, rectangleBottomLeft.y)
    candidates.filterNot(
      candidate => rectangleTopEdge.containsNotRight(candidate) || rectangleLeftEdge.containsNotBottom(candidate)
    )
  }

  protected[solution] def getNewCandidateFromRectangleTopRight(
    rectangleTopRight: Coordinates,
    placement: Map[Rectangle, Coordinates]
  ): Option[Coordinates] = {
    if (rectangleTopRight.x == boxLength || isInSomeLeftEdge(rectangleTopRight, placement)) {
      Option.empty[Coordinates]
    } else {
      val bottomEdges = placement.toSeq.map {
        case (rectangle, coordinates) => getBottomEdge(rectangle, coordinates)
      }.appended(boxTopBorder)
      val topRightNewCandidateY = bottomEdges
        .filterNot(
          edge => edge.y > rectangleTopRight.y || edge.right <= rectangleTopRight.x || rectangleTopRight.x < edge.left
        )
        .map(_.y)
        .max
      Option(Coordinates(rectangleTopRight.x, topRightNewCandidateY))
    }
  }

  protected[solution] def getNewCandidateFromRectangleBottomLeft(
    rectangleBottomLeft: Coordinates,
    placement: Map[Rectangle, Coordinates]
  ): Option[Coordinates] = {
    if (rectangleBottomLeft.y == boxLength || isInSomeTopEdge(rectangleBottomLeft, placement)) {
      Option.empty[Coordinates]
    } else {
      val rightEdges = placement.toSeq.map {
        case (rectangle, coordinates) => getRightEdge(rectangle, coordinates)
      }.appended(boxLeftBorder)
      val bottomLeftNewCandidateX = rightEdges
        .filterNot(
          edge =>
            edge.x > rectangleBottomLeft.x || edge.bottom <= rectangleBottomLeft.y || rectangleBottomLeft.y < edge.top
        )
        .map(_.x)
        .max
      Option(Coordinates(bottomLeftNewCandidateX, rectangleBottomLeft.y))
    }
  }

  private def isInSomeLeftEdge(point: Coordinates, placement: Map[Rectangle, Coordinates]): Boolean = {
    val leftEdges = placement.toSeq.map {
      case (rectangle, coordinates) => getLeftEdge(rectangle, coordinates)
    }.appended(boxRightBorder)
    leftEdges.exists(_.containsNotBottom(point))
  }

  private def isInSomeRightEdge(point: Coordinates, placement: Map[Rectangle, Coordinates]): Boolean = {
    val rightEdges = placement.toSeq.map {
      case (rectangle, coordinates) => getRightEdge(rectangle, coordinates)
    }.appended(boxLeftBorder)
    rightEdges.exists(_.containsNotBottom(point))
  }

  private def isInSomeTopEdge(point: Coordinates, placement: Map[Rectangle, Coordinates]): Boolean = {
    val topEdges = placement.toSeq.map {
      case (rectangle, coordinates) => getTopEdge(rectangle, coordinates)
    }.appended(boxBottomBorder)
    topEdges.exists(_.containsNotRight(point))
  }

  private def isInSomeBottomEdge(point: Coordinates, placement: Map[Rectangle, Coordinates]): Boolean = {
    val bottomEdges = placement.toSeq.map {
      case (rectangle, coordinates) => getBottomEdge(rectangle, coordinates)
    }.appended(boxTopBorder)
    bottomEdges.exists(_.containsNotRight(point))
  }

  private def getLeftEdge(rectangle: Rectangle, coordinates: Coordinates): VerticalEdge = VerticalEdge(
    coordinates.x,
    coordinates.y,
    coordinates.y + rectangle.height
  )

  private def getRightEdge(rectangle: Rectangle, coordinates: Coordinates): VerticalEdge = VerticalEdge(
    coordinates.x + rectangle.width,
    coordinates.y,
    coordinates.y + rectangle.height
  )

  private def getTopEdge(rectangle: Rectangle, coordinates: Coordinates): HorizontalEdge = HorizontalEdge(
    coordinates.y,
    coordinates.x,
    coordinates.x + rectangle.width
  )
  private def getBottomEdge(rectangle: Rectangle, coordinates: Coordinates): HorizontalEdge = HorizontalEdge(
    coordinates.y + rectangle.height,
    coordinates.x,
    coordinates.x + rectangle.width
  )
  private val boxLeftBorder = VerticalEdge(0, 0, boxLength)
  private val boxRightBorder = VerticalEdge(boxLength, 0, boxLength)
  private val boxTopBorder = HorizontalEdge(0, 0, boxLength)

  private val boxBottomBorder = HorizontalEdge(boxLength, 0, boxLength)

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

trait TopLeftCandidates {
  val topLeftCandidates: Map[Int, SortedSet[Coordinates]]
}

private sealed trait Edge {
  def contains(point: Coordinates): Boolean
  def containsInner(point: Coordinates): Boolean
}

private case class VerticalEdge(x: Int, top: Int, bottom: Int) extends Edge {
  override def contains(point: Coordinates): Boolean = point.x == x && top <= point.y && point.y <= bottom
  override def containsInner(point: Coordinates): Boolean = point.x == x && top < point.y && point.y < bottom
  def containsNotBottom(point: Coordinates): Boolean = point.x == x && top <= point.y && point.y < bottom
}

private case class HorizontalEdge(y: Int, left: Int, right: Int) extends Edge {
  override def contains(point: Coordinates): Boolean = point.y == y && left <= point.x && point.x <= right
  override def containsInner(point: Coordinates): Boolean = point.y == y && left < point.x && point.x < right
  def containsNotRight(point: Coordinates): Boolean = point.y == y && left <= point.x && point.x < right
}
