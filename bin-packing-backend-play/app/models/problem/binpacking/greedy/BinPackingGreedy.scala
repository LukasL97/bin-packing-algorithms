package models.problem.binpacking.greedy

import models.algorithm.Greedy
import models.algorithm.SelectionHandler
import models.problem.binpacking.Box
import models.problem.binpacking.Coordinates
import models.problem.binpacking.Placing
import models.problem.binpacking.Rectangle
import models.problem.binpacking.BinPacking
import models.problem.binpacking.BinPackingSolution
import models.problem.binpacking.BinPackingSolutionValidator

trait BinPackingGreedy extends BinPacking {
  val selectionHandler: BinPackingSelectionHandler
  lazy val greedy = new Greedy[Rectangle, BinPackingSolution](selectionHandler)

  override def startSolution: BinPackingSolution = selectionHandler.startSolution
}

trait BinPackingSelectionHandler
    extends SelectionHandler[Rectangle, BinPackingSolution] with BinPackingSolutionValidator {

  val boxLength: Int

  override val startSolution: BinPackingSolution = BinPackingSolution(Map())

  override def placeCandidateInSolution(
    candidate: Rectangle,
    solution: BinPackingSolution
  ): BinPackingSolution = {
    val placementsPerBox = solution.placement.groupBy {
      case (rectangle, placing) => placing.box
    }.toSeq.sortBy {
      case (box, placement) => box.id
    }.map {
      case (box, placement) => box -> placement.map { case (rectangle, placing) => rectangle -> placing.coordinates }
    }
    BinPackingSolution(
      solution.placement + (candidate -> placeRectangleInFirstPossiblePosition(candidate, placementsPerBox))
    )
  }

  private def placeRectangleInFirstPossiblePosition(
    rectangle: Rectangle,
    placementsPerBox: Seq[(Box, Map[Rectangle, Coordinates])]
  ): Placing = {
    val maxBoxId = placementsPerBox.toMap.keys.map(_.id).maxOption.getOrElse(0)
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
          Box(maxBoxId + 1, boxLength),
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
