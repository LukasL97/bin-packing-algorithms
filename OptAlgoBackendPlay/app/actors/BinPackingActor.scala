package actors

import actors.executors.BinPackingGreedyExecutor
import actors.executors.BinPackingLocalSearchExecutor
import akka.actor.Actor
import com.google.inject.Inject
import dao.BinPackingSolutionStepDAO
import models.problem.binpacking.greedy.BinPackingGreedy
import models.problem.binpacking.localsearch.BinPackingLocalSearch

object BinPackingActor {
  trait Factory {
    def apply(): Actor
  }
}

class BinPackingActor @Inject()(val dao: BinPackingSolutionStepDAO) extends Actor {
  override def receive: Receive = {
    case (runId: String, binPacking: BinPackingLocalSearch) =>
      val executor = new BinPackingLocalSearchExecutor(dao)
      executor.execute(runId, binPacking)
    case (runId: String, binPacking: BinPackingGreedy) =>
      val executor = new BinPackingGreedyExecutor(dao)
      executor.execute(runId, binPacking)
  }
}
