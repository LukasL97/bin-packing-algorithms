package actors

import actors.executors.BinPackingGreedyExecutor
import actors.executors.BinPackingLocalSearchExecutor
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import com.google.inject.Inject
import models.problem.binpacking.greedy.basic.BasicBinPackingGreedy
import models.problem.binpacking.greedy.candidatesupported.CandidateSupportedBinPackingGreedy
import models.problem.binpacking.localsearch.BinPackingLocalSearch
import models.problem.binpacking.solution.SimpleBinPackingSolution
import models.problem.binpacking.solution.TopLeftFirstBinPackingSolution

object BinPackingActor {
  trait Factory {
    def apply(): Actor
  }
}

class BinPackingActor @Inject()(
  val system: ActorSystem,
  val dumperFactory: SolutionStepDumper.Factory
) extends Actor {

  override def receive: Receive = {
    case (runId: String, binPacking: BinPackingLocalSearch[SimpleBinPackingSolution]) =>
      val dumper = createSolutionStepDumper(runId)
      val executor = new BinPackingLocalSearchExecutor[SimpleBinPackingSolution](dumper)
      executor.execute(runId, binPacking)
    case (runId: String, binPacking: BasicBinPackingGreedy) =>
      val dumper = createSolutionStepDumper(runId)
      val executor = new BinPackingGreedyExecutor[SimpleBinPackingSolution](dumper)
      executor.execute(runId, binPacking)
    case (runId: String, binPacking: CandidateSupportedBinPackingGreedy) =>
      val dumper = createSolutionStepDumper(runId)
      val executor = new BinPackingGreedyExecutor[TopLeftFirstBinPackingSolution](dumper)
      executor.execute(runId, binPacking)
  }

  private def createSolutionStepDumper(runId: String): ActorRef = {
    system.actorOf(
      Props(dumperFactory.apply()),
      s"solution-step-dumper-$runId"
    )
  }
}
