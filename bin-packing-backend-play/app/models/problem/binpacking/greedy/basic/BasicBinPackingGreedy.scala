package models.problem.binpacking.greedy.basic

import models.problem.binpacking.BinPackingTopLeftFirstPlacing
import models.problem.binpacking.greedy.BinPackingGreedy
import models.problem.binpacking.greedy.BinPackingSelectionHandler
import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.SimpleBinPackingSolution

trait BasicBinPackingGreedy extends BinPackingGreedy[SimpleBinPackingSolution] {
  override val selectionHandler: BasicBinPackingSelectionHandler

  override def startSolution: SimpleBinPackingSolution = selectionHandler.startSolution
}

trait BasicBinPackingSelectionHandler
    extends BinPackingSelectionHandler[SimpleBinPackingSolution] with BinPackingTopLeftFirstPlacing {

  override val startSolution: SimpleBinPackingSolution = SimpleBinPackingSolution(boxLength)

  override def placeCandidateInSolution(
    candidate: Rectangle,
    solution: SimpleBinPackingSolution
  ): SimpleBinPackingSolution = {
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
            if (boxIsFull(placement)) {
              Option.empty[(Rectangle, Placing)]
            } else {
              placeRectangleInBoxAtMostTopLeftPoint(rectangle, placement, considerRotation = true).map {
                case (rectangle, coordinates) => rectangle -> Placing(Box(boxId, boxLength), coordinates)
              }
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

  private def boxIsFull(placement: Map[Rectangle, Coordinates]): Boolean = {
    val rectangleArea = placement.keys.toSeq.map(rectangle => rectangle.width * rectangle.height).sum
    val boxArea = boxLength * boxLength
    rectangleArea == boxArea
  }

}
