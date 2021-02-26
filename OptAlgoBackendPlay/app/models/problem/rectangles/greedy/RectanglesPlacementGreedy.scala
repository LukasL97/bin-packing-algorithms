package models.problem.rectangles.greedy

import models.algorithm.Greedy
import models.algorithm.SelectionHandler
import models.problem.rectangles.Box
import models.problem.rectangles.Coordinates
import models.problem.rectangles.Placing
import models.problem.rectangles.Rectangle
import models.problem.rectangles.RectanglesPlacement
import models.problem.rectangles.RectanglesPlacementSolution
import models.problem.rectangles.RectanglesPlacementSolutionValidator

trait RectanglesPlacementGreedy extends RectanglesPlacement {
  val selectionHandler: RectanglesPlacementSelectionHandler
  lazy val greedy = new Greedy[Rectangle, RectanglesPlacementSolution](selectionHandler)
}

trait RectanglesPlacementSelectionHandler
    extends SelectionHandler[Rectangle, RectanglesPlacementSolution] with RectanglesPlacementSolutionValidator {

  val boxLength: Int

  override val startSolution: RectanglesPlacementSolution = RectanglesPlacementSolution(Map())

  override def placeCandidateInSolution(
    candidate: Rectangle,
    solution: RectanglesPlacementSolution
  ): RectanglesPlacementSolution = {
    val placementsPerBox = solution.placement.groupBy {
      case (rectangle, placing) => placing.box
    }.toSeq.sortBy {
      case (box, placement) => box.id
    }.map {
      case (box, placement) => box -> placement.map { case (rectangle, placing) => rectangle -> placing.coordinates }
    }
    RectanglesPlacementSolution(
      solution.placement + (candidate -> placeRectangleInFirstPossiblePosition(candidate, placementsPerBox))
    )
  }

  private def placeRectangleInFirstPossiblePosition(
    rectangle: Rectangle,
    placementsPerBox: Seq[(Box, Map[Rectangle, Coordinates])]
  ): Placing = {
    val maxBoxId = placementsPerBox.toMap.keys.map(_.id).max
    placementsPerBox
      .foldLeft(Option.empty[Placing]) {
        case (foundPlacing, (box, placement)) =>
          foundPlacing.orElse(
            placeRectangleInBoxAtMostTopLeftPoint(rectangle, placement)
              .map(Placing(box, _))
          )
      }
      .getOrElse(
        Placing(
          Box(maxBoxId + 1, boxLength, boxLength),
          Coordinates(0, 0)
        )
      )
  }

  private def placeRectangleInBoxAtMostTopLeftPoint(
    rectangle: Rectangle,
    placement: Map[Rectangle, Coordinates]
  ): Option[Coordinates] = {
    coordinatesInTopLeftFirstOrder.find { coordinates =>
      val newPlacement = placement + (rectangle -> coordinates)
      allRectanglesInBoundsForSingleBox(newPlacement, boxLength) && allRectanglesDisjunctiveInSingleBox(newPlacement)
    }
  }

  private implicit val topLeftFirstOrdering: Ordering[Coordinates] = (left: Coordinates, right: Coordinates) => {
    (left.x + left.y) - (right.x + right.y) match {
      case 0 => left.x - right.x
      case other => other
    }
  }

  private lazy val coordinatesInTopLeftFirstOrder = (0 until boxLength)
    .flatMap(x => (0 until boxLength).map(y => Coordinates(x, y)))
    .sorted

}
