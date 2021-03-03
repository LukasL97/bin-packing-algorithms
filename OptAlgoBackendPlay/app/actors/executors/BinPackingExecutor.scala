package actors.executors

import models.problem.binpacking.BinPacking

trait BinPackingExecutor[A <: BinPacking] {
  def execute(runId: String, binPacking: A): Unit
}
