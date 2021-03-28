package models.problem.binpacking.utils

import models.problem.binpacking.solution.Rectangle

trait RectangleSizeOrdering {

  implicit val sizeOrdering: Ordering[Rectangle] = new Ordering[Rectangle] {

    private def size(rectangle: Rectangle): Int = rectangle.width * rectangle.height

    override def compare(x: Rectangle, y: Rectangle): Int = size(x) - size(y)
  }
}
