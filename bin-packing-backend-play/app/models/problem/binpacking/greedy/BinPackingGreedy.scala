package models.problem.binpacking.greedy

import models.algorithm.Greedy
import models.algorithm.SelectionHandler
import models.problem.binpacking.BinPacking
import models.problem.binpacking.BinPackingTopLeftFirstPlacing
import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.SimpleBinPackingSolution

trait BinPackingGreedy extends BinPacking {
  val selectionHandler: BinPackingSelectionHandler
  lazy val greedy = new Greedy[Rectangle, BinPackingSolution](selectionHandler)

  override def startSolution: BinPackingSolution = selectionHandler.startSolution
}

trait BinPackingSelectionHandler
    extends SelectionHandler[Rectangle, BinPackingSolution] with BinPackingTopLeftFirstPlacing {

  override val startSolution: BinPackingSolution = SimpleBinPackingSolution(Map())

  override def placeCandidateInSolution(
    candidate: Rectangle,
    solution: BinPackingSolution
  ): BinPackingSolution = {
    val (rectangle, placing) = placeRectangleInFirstPossiblePosition(candidate, solution.getPlacementsPerBox)
    solution.updated(rectangle, placing)
  }

  private def placeRectangleInFirstPossiblePosition(
    rectangle: Rectangle,
    placementsPerBox: Map[Int, Map[Rectangle, Coordinates]]
  ): (Rectangle, Placing) = {
    val maxBoxId = placementsPerBox.keys.maxOption.getOrElse(0)
    placementsPerBox.toSeq.sortBy {
      case (boxId, _) => boxId
    }.foldLeft(Option.empty[(Rectangle, Placing)]) {
        case (foundPlacing, (boxId, placement)) =>
          foundPlacing.orElse(
            placeRectangleInBoxAtMostTopLeftPoint(rectangle, placement, considerRotation = true).map {
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
  }

}
