package actors

import actors.executors.BinPackingExecutor
import actors.executors.BinPackingGreedyExecutor
import actors.executors.BinPackingLocalSearchExecutor
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import com.google.inject.Inject
import models.problem.binpacking.BinPacking
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
    case (runId: String, binPacking: BinPacking, timeLimit: Option[Int]) =>
      val dumper = createSolutionStepDumper(runId)
      val executor = BinPackingExecutorProvider.get(binPacking, runId, dumper, timeLimit)
      executor.execute()
  }

  private def createSolutionStepDumper(runId: String): ActorRef = {
    system.actorOf(
      Props(dumperFactory.apply()),
      s"solution-step-dumper-$runId"
    )
  }
}

private object BinPackingExecutorProvider {
  def get(
    binPacking: BinPacking,
    runId: String,
    dumper: ActorRef,
    timeLimit: Option[Int]
  ): BinPackingExecutor = binPacking match {
    case binPacking: BinPackingLocalSearch[_] =>
      new BinPackingLocalSearchExecutor(binPacking, runId, dumper, timeLimit)
    case binPacking: BinPackingGreedy[_] =>
      new BinPackingGreedyExecutor(binPacking, runId, dumper)
  }
}
