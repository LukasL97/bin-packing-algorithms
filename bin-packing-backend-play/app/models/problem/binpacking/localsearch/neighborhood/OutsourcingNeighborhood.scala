package models.problem.binpacking.localsearch.neighborhood

import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing

import scala.collection.View

trait OutsourcingNeighborhood {

  val boxLength: Int

  def createOutsourcingNeighborhood(solution: BinPackingSolution): View[BinPackingSolution] = {
    val boxIds = solution.placement.values.map(_.box.id)
    val boxIdsWithOnlyOneRectangle = boxIds
      .groupBy(id => id)
      .map {
        case (id, occurrences) => id -> occurrences.size
      }
      .collect {
        case (id, count) if count == 1 => id
      }
      .toSeq
    solution.placement.view.collect {
      case (rectangle, Placing(box, _)) if !boxIdsWithOnlyOneRectangle.contains(box.id) =>
        solution.updated(rectangle, Placing(Box(boxIds.max + 1, boxLength), Coordinates(0, 0)))
    }
  }

}
