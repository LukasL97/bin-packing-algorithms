package models.problem.binpacking.utils

import models.problem.binpacking.BinPackingSolution
import models.problem.binpacking.Coordinates
import models.problem.binpacking.Rectangle

trait BinPackingSolutionUtil {

  def getPlacementsPerBox(solution: BinPackingSolution): Map[Int, Map[Rectangle, Coordinates]] = {
    solution.placement.groupBy {
      case (rectangle, placing) => placing.box
    }.toSeq.sortBy {
      case (box, placement) => box.id
    }.map {
      case (box, placement) => box.id -> placement.map { case (rectangle, placing) => rectangle -> placing.coordinates }
    }.toMap
  }

  def getPlacementInSingleBox(solution: BinPackingSolution, boxId: Int): Map[Rectangle, Coordinates] = {
    getPlacementsPerBox(solution)(boxId)
  }

}
