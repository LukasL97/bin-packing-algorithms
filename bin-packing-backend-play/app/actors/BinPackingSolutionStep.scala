package actors

import models.problem.binpacking.solution.BinPackingSolution

case class BinPackingSolutionStep(
  runId: String,
  step: Int,
  solution: BinPackingSolution,
  finished: Boolean = false
)

object BinPackingSolutionStep {
  def startStep(runId: String, solution: BinPackingSolution): BinPackingSolutionStep = {
    BinPackingSolutionStep(
      runId,
      0,
      solution
    )
  }
}
