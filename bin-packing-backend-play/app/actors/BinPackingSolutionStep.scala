package actors

import models.problem.binpacking.solution.BinPackingSolutionRepresentation

case class BinPackingSolutionStep(
  runId: String,
  step: Int,
  solution: BinPackingSolutionRepresentation,
  finished: Boolean = false
)

object BinPackingSolutionStep {
  def startStep(runId: String, solution: BinPackingSolutionRepresentation): BinPackingSolutionStep = {
    BinPackingSolutionStep(
      runId,
      0,
      solution
    )
  }
}
