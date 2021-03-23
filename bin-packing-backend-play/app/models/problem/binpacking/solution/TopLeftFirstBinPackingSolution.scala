package models.problem.binpacking.solution

import models.problem.binpacking.BinPackingTopLeftFirstPlacing

object TopLeftFirstBinPackingSolution {
  def apply(boxLength: Int): TopLeftFirstBinPackingSolution = new TopLeftFirstBinPackingSolution(
    Map.empty[Rectangle, Placing],
    Map.empty[Int, Seq[TopLeftCandidate]],
    boxLength
  )
}

case class TopLeftFirstBinPackingSolution(
  override val placement: Map[Rectangle, Placing],
  override val topLeftCandidates: Map[Int, Seq[TopLeftCandidate]],
  override val boxLength: Int
) extends BinPackingSolution with TopLeftCandidates with BinPackingTopLeftFirstPlacing {

  override def asSimpleSolution: SimpleBinPackingSolution = SimpleBinPackingSolution(placement)

  override def updated(rectangle: Rectangle, placing: Placing): BinPackingSolution = throw new NotImplementedError

  override def reset(placement: Map[Rectangle, Placing]): BinPackingSolution = throw new NotImplementedError

  def placeTopLeftFirst(rectangle: Rectangle): TopLeftFirstBinPackingSolution = {
    val placementsPerBox = getPlacementsPerBox
    val sortedPlacementsPerBox = placementsPerBox.toSeq.sortBy {
      case (boxId, _) => boxId
    }
    val maxBoxId = sortedPlacementsPerBox.lastOption.map(_._1).getOrElse(0)
    val (placedRectangle, placing) = sortedPlacementsPerBox
      .foldLeft(Option.empty[(Rectangle, Placing)]) {
        case (foundPlacing, (boxId, placement)) =>
          foundPlacing.orElse(
            placeRectangleInBoxAtMostTopLeftPoint(
              rectangle,
              placement,
              considerRotation = true,
              candidateCoordinates = Option(topLeftCandidates(boxId).map(_.coordinates))
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
    val updatedPlacement = placement.updated(placedRectangle, placing)
    val rectangleTopRight = Coordinates(placing.coordinates.x + placedRectangle.width, placing.coordinates.y)
    val rectangleBottomLeft = Coordinates(placing.coordinates.x, placing.coordinates.y + placedRectangle.height)
    val updatedCandidates: Map[Int, Seq[TopLeftCandidate]] = if (placing.box.id > maxBoxId) {
      val newBoxCandidates = Seq(rectangleBottomLeft, rectangleTopRight)
        .sortBy(c => c.x + c.y)
        .map(coordinates => TopLeftCandidate(coordinates, Set(coordinates)))
      topLeftCandidates.updated(placing.box.id, newBoxCandidates)
    } else {
      topLeftCandidates.updated(
        placing.box.id,
        updateCandidates(
          placing.coordinates,
          rectangleTopRight,
          rectangleBottomLeft,
          topLeftCandidates(placing.box.id),
          placementsPerBox(placing.box.id)
        )
      )
    }
    TopLeftFirstBinPackingSolution(
      updatedPlacement,
      updatedCandidates,
      boxLength
    )
  }

  private def updateCandidates(
    rectangleTopLeft: Coordinates,
    rectangleTopRight: Coordinates,
    rectangleBottomLeft: Coordinates,
    candidates: Seq[TopLeftCandidate],
    oldPlacement: Map[Rectangle, Coordinates]
  ): Seq[TopLeftCandidate] = {
    println(s"BOTTOMLEFT: $rectangleBottomLeft")
    println(s"TOPRIGHT: $rectangleTopRight")
    val candidatesWithPlacedCoordinatesRemoved = candidates.filterNot(_.coordinates == rectangleTopLeft)
    assert(candidatesWithPlacedCoordinatesRemoved.size == candidates.size - 1)
    val candidatesWithCoveredCoordinatesLifted = liftCandidatesCoveredByNewRectangle(
      rectangleTopLeft,
      rectangleTopRight,
      rectangleBottomLeft,
      candidatesWithPlacedCoordinatesRemoved
    )
    val newCandidates = Seq(
      getNewCandidateFromRectangleTopRight(rectangleTopRight, oldPlacement),
      getNewCandidateFromRectangleBottomLeft(rectangleBottomLeft, oldPlacement)
    ).flatten
    newCandidates.foldLeft(candidatesWithCoveredCoordinatesLifted) {
      case (updatesCandidates, newCandidate) => sortInCandidate(updatesCandidates, newCandidate)
    }
  }

  private def liftCandidatesCoveredByNewRectangle(
    rectangleTopLeft: Coordinates,
    rectangleTopRight: Coordinates,
    rectangleBottomLeft: Coordinates,
    candidates: Seq[TopLeftCandidate]
  ): Seq[TopLeftCandidate] = {
    val rectangleTopEdge = HorizontalEdge(rectangleTopLeft.y, rectangleTopLeft.x, rectangleTopRight.x)
    val rectangleLeftEdge = VerticalEdge(rectangleTopLeft.x, rectangleTopLeft.y, rectangleBottomLeft.y)
    val (liftedCandidates, unchangedCandidates) = candidates.map {
      case candidate if rectangleTopEdge.containsInner(candidate.coordinates) =>
        LiftedCandidate(
          TopLeftCandidate(Coordinates(candidate.coordinates.x, rectangleLeftEdge.bottom), candidate.producers)
        )
      case candidate if rectangleLeftEdge.containsInner(candidate.coordinates) =>
        LiftedCandidate(
          TopLeftCandidate(Coordinates(rectangleTopEdge.right, candidate.coordinates.y), candidate.producers)
        )
      case coordinates => UnchangedCandidate(coordinates)
    }.partition(_.isInstanceOf[LiftedCandidate])
    liftedCandidates.map(_.candidate).foldLeft(unchangedCandidates.map(_.candidate)) {
      case (updatedCandidates, liftedCandidate) => sortInCandidate(updatedCandidates, liftedCandidate)
    }
  }

  protected[solution] def getNewCandidateFromRectangleTopRight(
    rectangleTopRight: Coordinates,
    placement: Map[Rectangle, Coordinates]
  ): Option[TopLeftCandidate] = {
    val leftEdges = placement.toSeq.map {
      case (rectangle, coordinates) => getLeftEdge(rectangle, coordinates)
    }.appended(boxRightBorder)
    if (leftEdges.exists(_.containsNotBottom(rectangleTopRight))) {
      Option.empty[TopLeftCandidate]
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
      Option(TopLeftCandidate(Coordinates(rectangleTopRight.x, topRightNewCandidateY), Set(rectangleTopRight)))
    }
  }

  protected[solution] def getNewCandidateFromRectangleBottomLeft(
    rectangleBottomLeft: Coordinates,
    placement: Map[Rectangle, Coordinates]
  ): Option[TopLeftCandidate] = {
    val topEdges = placement.toSeq.map {
      case (rectangle, coordinates) => getTopEdge(rectangle, coordinates)
    }.appended(boxBottomBorder)
    if (topEdges.exists(_.containsNotRight(rectangleBottomLeft))) {
      Option.empty[TopLeftCandidate]
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
      Option(TopLeftCandidate(Coordinates(bottomLeftNewCandidateX, rectangleBottomLeft.y), Set(rectangleBottomLeft)))
    }
  }

  protected[solution] def sortInCandidate(
    orderedCandidates: Seq[TopLeftCandidate],
    newCandidate: TopLeftCandidate
  ): Seq[TopLeftCandidate] = {
    if (orderedCandidates.contains(newCandidate)) {
      orderedCandidates
    } else {
      val index =
        orderedCandidates.indexWhere(
          candidate =>
            candidate.coordinates.x + candidate.coordinates.y >= newCandidate.coordinates.x + newCandidate.coordinates.y
        )
      if (index == -1) {
        orderedCandidates.appended(newCandidate)
      } else {
        orderedCandidates.slice(0, index).appended(newCandidate) ++ orderedCandidates
          .slice(index, orderedCandidates.size)
      }
    }
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

}

trait TopLeftCandidates {
  val topLeftCandidates: Map[Int, Seq[TopLeftCandidate]]
}

case class TopLeftCandidate(
  coordinates: Coordinates,
  producers: Set[Coordinates]
)

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

private sealed trait CandidateLiftingResult {
  val candidate: TopLeftCandidate
}
private case class LiftedCandidate(candidate: TopLeftCandidate) extends CandidateLiftingResult
private case class UnchangedCandidate(candidate: TopLeftCandidate) extends CandidateLiftingResult
