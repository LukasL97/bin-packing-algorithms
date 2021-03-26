package models.problem.binpacking.localsearch.neighborhood

import metrics.Metrics
import models.problem.binpacking.BinPackingTopLeftFirstPlacing
import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.transformation.RectanglePlacingUpdateSupport
import models.problem.binpacking.solution.transformation.SquashingSupport

import scala.collection.View

class BoxPullUpNeighborhood[A <: RectanglePlacingUpdateSupport[A] with SquashingSupport[A]](
  override val boxLength: Int
) extends BinPackingTopLeftFirstPlacing with Metrics {

  def createBoxPullUpNeighborhood(solution: A): View[A] = {
    solution.placement.view.collect {
      case (rectangle, Placing(Box(id, length), _)) if id > 1 =>
        withTimer("box-pull-up-neighborhood") {
          placeRectangleInBoxAtMostTopLeftPoint(
            rectangle,
            solution.getPlacementsPerBox(id - 1),
            considerRotation = true
          ).map {
            case (rectangle, coordinates) =>
              solution.updated(rectangle, Placing(Box(id - 1, length), coordinates)).squashed
          }
        }
    }.flatten
  }

}
