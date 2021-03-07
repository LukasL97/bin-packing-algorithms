package models.problem.binpacking.localsearch.neighborhood

import models.problem.binpacking.BinPackingSolution
import models.problem.binpacking.BinPackingTopLeftFirstPlacing
import models.problem.binpacking.Box
import models.problem.binpacking.Placing
import models.problem.binpacking.utils.BinPackingSolutionUtil

trait BoxPullUpNeighborhood extends BinPackingTopLeftFirstPlacing with BinPackingSolutionUtil {

  def createBoxPullUpNeighborhood(solution: BinPackingSolution): Set[BinPackingSolution] = {
    val placementPerBox = getPlacementsPerBox(solution)
    solution.placement.collect {
      case (rectangle, Placing(Box(id, length), _)) if id > 1 =>
        placeRectangleInBoxAtMostTopLeftPoint(rectangle, placementPerBox(id - 1)).map(
          coordinates =>
            BinPackingSolution(
              solution.placement.updated(
                rectangle,
                Placing(Box(id - 1, length), coordinates)
              )
          )
        )
    }.flatten.map(shiftUpSolution).toSet
  }

  private def shiftUpSolution(solution: BinPackingSolution): BinPackingSolution = {
    val usedBoxIds = solution.placement.values.map(_.box.id).toSet
    val skippedBoxIds = (1 to usedBoxIds.max).filter(!usedBoxIds.contains(_))
    skippedBoxIds match {
      case Seq() => solution
      case Seq(skippedBoxId) =>
        BinPackingSolution(
          solution.placement.map {
            case (rectangle, Placing(box, coordinates)) if box.id <= skippedBoxId =>
              rectangle -> Placing(box, coordinates)
            case (rectangle, Placing(box, coordinates)) if box.id > skippedBoxId =>
              rectangle -> Placing(box.copy(id = box.id - 1), coordinates)
          }
        )
      case ids if ids.size > 1 => throw new RuntimeException("More than 1 box emptied in one neighborhood step")
    }
  }

}
