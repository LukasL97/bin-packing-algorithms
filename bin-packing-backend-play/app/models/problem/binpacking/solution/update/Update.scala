package models.problem.binpacking.solution.update

sealed trait Update

case class StartSolution() extends Update

case class RectanglesChanged(rectangleIds: Set[Int]) extends Update

