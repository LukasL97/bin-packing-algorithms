package models.problem.binpacking.localsearch.neighborhood

import models.problem.binpacking.BinPackingTopLeftFirstPlacing
import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.BinPackingSolution

import scala.collection.View

trait BoxPullUpNeighborhood extends BinPackingTopLeftFirstPlacing {

  def createBoxPullUpNeighborhood(solution: BinPackingSolution): View[BinPackingSolution] = {
    solution.placement.view.collect {
      case (rectangle, Placing(Box(id, length), _)) if id > 1 =>
        placeRectangleInBoxAtMostTopLeftPoint(rectangle, solution.getPlacementsPerBox(id - 1), considerRotation = true).map {
          case (rectangle, coordinates) => solution.updated(rectangle, Placing(Box(id - 1, length), coordinates))
        }
    }.flatten.map(_.squashed)
  }

}
