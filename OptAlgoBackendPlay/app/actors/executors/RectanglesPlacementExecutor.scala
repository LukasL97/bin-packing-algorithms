package actors.executors

import models.problem.rectangles.RectanglesPlacement

trait RectanglesPlacementExecutor[A <: RectanglesPlacement] {
  def execute(runId: String, rectanglesPlacement: A): Unit
}
