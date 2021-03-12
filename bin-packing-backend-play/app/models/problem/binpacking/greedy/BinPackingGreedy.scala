package models.problem.binpacking.greedy

import models.algorithm.Greedy
import models.algorithm.SelectionHandler
import models.problem.binpacking.BinPacking
import models.problem.binpacking.BinPackingTopLeftFirstPlacing
import models.problem.binpacking.Box
import models.problem.binpacking.Coordinates
import models.problem.binpacking.Placing
import models.problem.binpacking.Rectangle
import models.problem.binpacking.SimpleBinPackingSolution

trait BinPackingGreedy extends BinPacking {
  val selectionHandler: BinPackingSelectionHandler
  lazy val greedy = new Greedy[Rectangle, SimpleBinPackingSolution](selectionHandler)

  override def startSolution: SimpleBinPackingSolution = selectionHandler.startSolution
}

trait BinPackingSelectionHandler
    extends SelectionHandler[Rectangle, SimpleBinPackingSolution] with BinPackingTopLeftFirstPlacing {

  override val startSolution: SimpleBinPackingSolution = SimpleBinPackingSolution(Map())

  override def placeCandidateInSolution(
    candidate: Rectangle,
    solution: SimpleBinPackingSolution
  ): SimpleBinPackingSolution = {
    SimpleBinPackingSolution(
      solution.placement + placeRectangleInFirstPossiblePosition(candidate, solution.getPlacementsPerBox)
    )
  }

  private def placeRectangleInFirstPossiblePosition(
    rectangle: Rectangle,
    placementsPerBox: Map[Int, Map[Rectangle, Coordinates]]
  ): (Rectangle, Placing) = {
    val maxBoxId = placementsPerBox.keys.maxOption.getOrElse(0)
    placementsPerBox
      .foldLeft(Option.empty[(Rectangle, Placing)]) {
        case (foundPlacing, (boxId, placement)) =>
          foundPlacing.orElse(
            placeRectangleInBoxAtMostTopLeftPoint(rectangle, placement, considerRotation = true)
              .map { case (rectangle, coordinates) => rectangle -> Placing(Box(boxId, boxLength), coordinates) }
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
