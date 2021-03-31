package models.problem.binpacking.solution

import models.problem.binpacking.BinPackingSolutionValidator
import models.problem.binpacking.solution.initialization.EmptySolutionInitializer

import scala.collection.SortedSet

object OverlappingTopLeftFirstBinPackingSolution
    extends EmptySolutionInitializer[OverlappingTopLeftFirstBinPackingSolution] {

  override def apply(boxLength: Int): OverlappingTopLeftFirstBinPackingSolution =
    new OverlappingTopLeftFirstBinPackingSolution(
      Map.empty[Rectangle, Placing],
      Map.empty[Int, SortedSet[Coordinates]],
      Map.empty[Int, Set[Overlapping]],
      boxLength
    )
}

case class OverlappingTopLeftFirstBinPackingSolution(
  override val placement: Map[Rectangle, Placing],
  override val topLeftCandidates: Map[Int, SortedSet[Coordinates]],
  override val overlappings: Map[Int, Set[Overlapping]],
  boxLength: Int
) extends AbstractTopLeftFirstBinPackingSolution with Overlappings with BinPackingSolutionValidator {

  override def asSimpleSolution: SimpleBinPackingSolution = SimpleBinPackingSolution(placement)

  def removeRectangleFromBox(rectangleId: Int, boxId: Int): Overlappings = {
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
        topLeftCandidates = topLeftCandidates.removed(boxId),
        overlappings = overlappings.removed(boxId)
      )
    } else {
      val updatedBoxCandidates =
        updateCandidatesOnRectangleRemoval(rectangle, coordinates, topLeftCandidates(boxId), updatedBoxPlacement)
      val updatedBoxOverlappings = overlappings(boxId).filterNot {
        case Overlapping(rectangleA, _, rectangleB, _, _) => rectangleA == rectangle || rectangleB == rectangle
      }
      copy(
        placement = updatedPlacement,
        topLeftCandidates = topLeftCandidates.updated(boxId, updatedBoxCandidates),
        overlappings = overlappings.updated(boxId, updatedBoxOverlappings)
      )
    }
  }

  def placeTopLeftFirst(rectangle: Rectangle, maxOverlap: Double): OverlappingTopLeftFirstBinPackingSolution = {
    val (placedRectangle, placing, newOverlappings) = findRectanglePlacing(rectangle, maxOverlap)
    val updatedPlacement = placement.updated(placedRectangle, placing)
    val updatedCandidates = updateCandidates(placedRectangle, placing)
    val updatedOverlappings = overlappings.updated(
      placing.box.id,
      overlappings.getOrElse(placing.box.id, Set.empty[Overlapping]) ++ newOverlappings
    )
    copy(
      placement = updatedPlacement,
      topLeftCandidates = updatedCandidates,
      overlappings = updatedOverlappings
    )
  }

  def placeTopLeftFirstInSpecificBox(
    rectangle: Rectangle,
    boxId: Int,
    maxOverlap: Double
  ): Option[OverlappingTopLeftFirstBinPackingSolution] = {
    findRectanglePlacingInSpecificBox(rectangle, boxId, maxOverlap).map {
      case (placedRectangle, coordinates, newOverlappings) =>
        val placing = Placing(Box(boxId, boxLength), coordinates)
        val updatedPlacement = placement.updated(placedRectangle, placing)
        val updatedCandidates = updateCandidates(placedRectangle, placing)
        val updatedOverlappings = overlappings.updated(
          boxId,
          overlappings.getOrElse(boxId, Set.empty[Overlapping]) ++ newOverlappings
        )
        copy(
          placement = updatedPlacement,
          topLeftCandidates = updatedCandidates,
          overlappings = updatedOverlappings
        )
    }
  }

  private def findRectanglePlacingInSpecificBox(
    rectangle: Rectangle,
    boxId: Int,
    maxOverlap: Double
  ): Option[(Rectangle, Coordinates, Set[Overlapping])] = {
    placeRectangleInBoxAtMostTopLeftPoint(
      rectangle,
      getPlacementInSingleBox(boxId),
      topLeftCandidates.getOrElse(boxId, SortedSet(Coordinates(0, 0))),
      maxOverlap
    )
  }

  private def findRectanglePlacing(
    rectangle: Rectangle,
    maxOverlap: Double
  ): (Rectangle, Placing, Set[Overlapping]) = {
    val placementsPerBox = getPlacementsPerBox
    val sortedPlacementsPerBox = placementsPerBox.toSeq.sortBy {
      case (boxId, _) => boxId
    }
    val maxBoxId = sortedPlacementsPerBox.lastOption.map(_._1).getOrElse(0)
    sortedPlacementsPerBox
      .foldLeft(Option.empty[(Rectangle, Placing, Set[Overlapping])]) {
        case (foundPlacing, (boxId, placement)) =>
          foundPlacing.orElse {
            placeRectangleInBoxAtMostTopLeftPoint(
              rectangle,
              placement,
              candidates = topLeftCandidates(boxId),
              maxOverlap
            ).map {
              case (rectangle, coordinates, overlappings) =>
                (rectangle, Placing(Box(boxId, boxLength), coordinates), overlappings)
            }
          }
      }
      .getOrElse(
        (
          rectangle,
          Placing(
            Box(maxBoxId + 1, boxLength),
            Coordinates(0, 0),
          ),
          Set.empty[Overlapping]
        )
      )
  }

  private def placeRectangleInBoxAtMostTopLeftPoint(
    rectangle: Rectangle,
    placement: Map[Rectangle, Coordinates],
    candidates: SortedSet[Coordinates],
    maxOverlap: Double
  ): Option[(Rectangle, Coordinates, Set[Overlapping])] = {
    val rotatedRectangle = rectangle.rotated
    candidates.foldLeft(Option.empty[(Rectangle, Coordinates, Set[Overlapping])]) {
      case (Some(placing), _) => Some(placing)
      case (None, coordinates) =>
        placeRectangleAtSpecificCoordinates(rectangle, coordinates, placement, maxOverlap)
          .orElse(placeRectangleAtSpecificCoordinates(rotatedRectangle, coordinates, placement, maxOverlap))
    }
  }

  private def placeRectangleAtSpecificCoordinates(
    rectangle: Rectangle,
    coordinates: Coordinates,
    placement: Map[Rectangle, Coordinates],
    maxOverlap: Double
  ): Option[(Rectangle, Coordinates, Set[Overlapping])] = {
    if (inBounds(rectangle, coordinates, boxLength)) {
      val overlappings = getNewOverlappings(rectangle, coordinates, placement)
      if (overlappings.map(_.overlap).maxOption.getOrElse(0.0) > maxOverlap) {
        Option.empty[(Rectangle, Coordinates, Set[Overlapping])]
      } else {
        Option((rectangle, coordinates, overlappings))
      }
    } else {
      Option.empty[(Rectangle, Coordinates, Set[Overlapping])]
    }
  }

  private def getNewOverlappings(
    rectangle: Rectangle,
    coordinates: Coordinates,
    placement: Map[Rectangle, Coordinates]
  ): Set[Overlapping] = {
    placement.flatMap {
      case (existingRectangle, existingCoordinates) =>
        val overlap = calculateOverlap((rectangle, coordinates), (existingRectangle, existingCoordinates))
        if (overlap > 0) {
          Option(Overlapping(rectangle, coordinates, existingRectangle, existingCoordinates, overlap))
        } else {
          Option.empty[Overlapping]
        }
    }.toSet
  }

  private def calculateOverlap(placingA: (Rectangle, Coordinates), placingB: (Rectangle, Coordinates)): Double = {
    val (rectangleA, coordinatesA) = placingA
    val (rectangleB, coordinatesB) = placingB
    val commonAreaTopLeft = Coordinates(
      Math.max(coordinatesA.x, coordinatesB.x),
      Math.max(coordinatesA.y, coordinatesB.y)
    )
    val commonAreaBottomRight = Coordinates(
      Math.min(coordinatesA.x + rectangleA.width, coordinatesB.x + rectangleB.width),
      Math.min(coordinatesA.y + rectangleA.height, coordinatesB.y + rectangleB.height)
    )
    val commonAreaWidth = commonAreaBottomRight.x - commonAreaTopLeft.x
    val commonAreaHeight = commonAreaBottomRight.y - commonAreaTopLeft.y
    if (commonAreaWidth > 0 && commonAreaHeight > 0) {
      (commonAreaWidth * commonAreaHeight).toDouble / Math.max(
        rectangleA.width * rectangleA.height,
        rectangleB.width * rectangleB.height
      )
    } else {
      0.0
    }
  }

  override protected def liftCandidatesShadowedByNewRectangleLeftEdge(
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
    candidates ++ liftedCandidates.filterNot(isInSomeRectangleNotRightOrBottom(_, placement))
  }

  override protected def liftCandidatesShadowedByNewRectangleTopEdge(
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
    candidates ++ liftedCandidates.filterNot(isInSomeRectangleNotRightOrBottom(_, placement))
  }

  override protected def dropCandidatesCoveredByNewRectangle(
    rectangleTopLeft: Coordinates,
    rectangleTopRight: Coordinates,
    rectangleBottomLeft: Coordinates,
    candidates: SortedSet[Coordinates]
  ): SortedSet[Coordinates] = {
    candidates.filterNot(
      candidate =>
        isInRectangleNotRightOrBottom(
          candidate,
          Rectangle(0, rectangleTopRight.x - rectangleTopLeft.x, rectangleBottomLeft.y - rectangleTopLeft.y),
          rectangleTopLeft
      )
    )
  }

  override protected[solution] def getNewCandidateFromRectangleTopRight(
    rectangleTopRight: Coordinates,
    placement: Map[Rectangle, Coordinates]
  ): Option[Coordinates] = {
    if (rectangleTopRight.x == boxLength || isInSomeRectangleNotRightOrBottom(rectangleTopRight, placement)) {
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

  override protected[solution] def getNewCandidateFromRectangleBottomLeft(
    rectangleBottomLeft: Coordinates,
    placement: Map[Rectangle, Coordinates]
  ): Option[Coordinates] = {
    if (rectangleBottomLeft.y == boxLength || isInSomeRectangleNotRightOrBottom(rectangleBottomLeft, placement)) {
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

  private def isInSomeRectangleNotRightOrBottom(
    point: Coordinates,
    placement: Map[Rectangle, Coordinates]
  ): Boolean = {
    placement.exists {
      case (rectangle, coordinates) => isInRectangleNotRightOrBottom(point, rectangle, coordinates)
    }
  }

  private def isInRectangleNotRightOrBottom(
    point: Coordinates,
    rectangle: Rectangle,
    coordinates: Coordinates
  ): Boolean = {
    point.x >= coordinates.x &&
    point.x < coordinates.x + rectangle.width &&
    point.y >= coordinates.y &&
    point.y < coordinates.y + rectangle.height
  }

}

trait Overlappings {
  val overlappings: Map[Int, Set[Overlapping]]
}

case class Overlapping(
  rectangleA: Rectangle,
  coordinatesA: Coordinates,
  rectangleB: Rectangle,
  coordinatesB: Coordinates,
  overlap: Double
)
