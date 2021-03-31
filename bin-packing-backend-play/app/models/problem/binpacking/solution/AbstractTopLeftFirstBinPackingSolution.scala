package models.problem.binpacking.solution

import models.problem.binpacking.BinPackingTopLeftFirstPlacing

import scala.collection.SortedSet

abstract class AbstractTopLeftFirstBinPackingSolution
    extends BinPackingSolution with TopLeftCandidates with BinPackingTopLeftFirstPlacing {

  val boxLength: Int

  override def asSimpleSolution: SimpleBinPackingSolution = SimpleBinPackingSolution(placement)

  protected def updateCandidatesOnRectangleRemoval(
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

  protected def updateCandidates(
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

  protected def updateCandidatesInPreviouslyExistingBox(
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

  protected def liftCandidatesShadowedByNewRectangleLeftEdge(
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

  protected def liftCandidatesShadowedByNewRectangleTopEdge(
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

  protected def dropCandidatesCoveredByNewRectangle(
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

  protected def getLeftEdge(rectangle: Rectangle, coordinates: Coordinates): VerticalEdge = VerticalEdge(
    coordinates.x,
    coordinates.y,
    coordinates.y + rectangle.height
  )

  protected def getRightEdge(rectangle: Rectangle, coordinates: Coordinates): VerticalEdge = VerticalEdge(
    coordinates.x + rectangle.width,
    coordinates.y,
    coordinates.y + rectangle.height
  )

  protected def getTopEdge(rectangle: Rectangle, coordinates: Coordinates): HorizontalEdge = HorizontalEdge(
    coordinates.y,
    coordinates.x,
    coordinates.x + rectangle.width
  )

  protected def getBottomEdge(rectangle: Rectangle, coordinates: Coordinates): HorizontalEdge = HorizontalEdge(
    coordinates.y + rectangle.height,
    coordinates.x,
    coordinates.x + rectangle.width
  )

  protected val boxLeftBorder: VerticalEdge = VerticalEdge(0, 0, boxLength)
  protected val boxRightBorder: VerticalEdge = VerticalEdge(boxLength, 0, boxLength)
  protected val boxTopBorder: HorizontalEdge = HorizontalEdge(0, 0, boxLength)
  protected val boxBottomBorder: HorizontalEdge = HorizontalEdge(boxLength, 0, boxLength)

}

trait TopLeftCandidates {
  val topLeftCandidates: Map[Int, SortedSet[Coordinates]]
}

protected sealed trait Edge {
  def contains(point: Coordinates): Boolean
  def containsInner(point: Coordinates): Boolean
}

protected case class VerticalEdge(x: Int, top: Int, bottom: Int) extends Edge {
  override def contains(point: Coordinates): Boolean = point.x == x && top <= point.y && point.y <= bottom
  override def containsInner(point: Coordinates): Boolean = point.x == x && top < point.y && point.y < bottom
  def containsNotBottom(point: Coordinates): Boolean = point.x == x && top <= point.y && point.y < bottom
}

protected case class HorizontalEdge(y: Int, left: Int, right: Int) extends Edge {
  override def contains(point: Coordinates): Boolean = point.y == y && left <= point.x && point.x <= right
  override def containsInner(point: Coordinates): Boolean = point.y == y && left < point.x && point.x < right
  def containsNotRight(point: Coordinates): Boolean = point.y == y && left <= point.x && point.x < right
}
