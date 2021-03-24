package models.problem.binpacking.utils

import models.problem.binpacking.solution.Coordinates

trait TopLeftFirstCoordinateOrdering {
  implicit val ordering: Ordering[Coordinates] = (left: Coordinates, right: Coordinates) => {
    (left.x + left.y) - (right.x + right.y) match {
      case 0 => left.x - right.x
      case other => other
    }
  }
}
