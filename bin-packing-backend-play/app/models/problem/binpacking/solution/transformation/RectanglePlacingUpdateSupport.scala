package models.problem.binpacking.solution.transformation

import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle

trait RectanglePlacingUpdateSupport[A <: RectanglePlacingUpdateSupport[A]] extends BinPackingSolution {
  def updated(rectangle: Rectangle, placing: Placing): A

  def updated(placement: Map[Rectangle, Placing]): A = {
    placement.foldLeft(this.asInstanceOf[A]) {
      case (updatedSolution, (rectangle, placing)) => updatedSolution.updated(rectangle, placing)
    }
  }
}
