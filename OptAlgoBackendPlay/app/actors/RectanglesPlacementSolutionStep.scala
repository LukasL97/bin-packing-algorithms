package actors

import models.problem.rectangles.RectanglesPlacementSolution

case class RectanglesPlacementSolutionStep(
  runId: String,
  step: Int,
  solution: RectanglesPlacementSolution
)
