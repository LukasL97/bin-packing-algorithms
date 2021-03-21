package actors

import actors.executors.BinPackingGreedyExecutor
import actors.executors.BinPackingLocalSearchExecutor
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import com.google.inject.Inject
import models.problem.binpacking.greedy.BinPackingGreedy
import models.problem.binpacking.localsearch.BinPackingLocalSearch

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
    case (runId: String, binPacking: BinPackingLocalSearch) =>
      val dumper = createSolutionStepDumper(runId)
      val executor = new BinPackingLocalSearchExecutor(dumper)
      executor.execute(runId, binPacking)
    case (runId: String, binPacking: BinPackingGreedy) =>
      val dumper = createSolutionStepDumper(runId)
      val executor = new BinPackingGreedyExecutor(dumper)
      executor.execute(runId, binPacking)
  }

  private def createSolutionStepDumper(runId: String): ActorRef = {
    system.actorOf(
      Props(dumperFactory.apply()),
      s"solution-step-dumper-$runId"
    )
  }
}
