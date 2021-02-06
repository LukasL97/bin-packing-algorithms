package actors

import models.problem.rectangles.RectanglesPlacementSolution

case class RectanglesPlacementSolutionStep(
  runId: String,
  step: Int,
  solution: RectanglesPlacementSolution
)

object RectanglesPlacementSolutionStep {
  def getStartStep(runId: String, solution: RectanglesPlacementSolution): RectanglesPlacementSolutionStep = {
    RectanglesPlacementSolutionStep(
      runId,
      0,
      solution
    )
  }
}
