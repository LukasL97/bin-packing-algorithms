package actors

import models.problem.binpacking.solution.SimpleBinPackingSolution

case class BinPackingSolutionStep(
  runId: String,
  step: Int,
  solution: SimpleBinPackingSolution,
  finished: Boolean = false
)

object BinPackingSolutionStep {
  def startStep(runId: String, solution: SimpleBinPackingSolution): BinPackingSolutionStep = {
    BinPackingSolutionStep(
      runId,
      0,
      solution
    )
  }
}
