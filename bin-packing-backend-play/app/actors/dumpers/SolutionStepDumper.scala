package actors.dumpers

import actors.BinPackingSolutionStep
import akka.actor.Actor
import com.google.inject.Inject
import dao.BinPackingSolutionStepDAO

object SolutionStepDumper {
  trait Factory {
    def apply(): Actor
  }
}

class SolutionStepDumper @Inject()(val dao: BinPackingSolutionStepDAO) extends Actor {
  override def receive: Receive = {
    case solutionStep: BinPackingSolutionStep => dao.dumpSolutionStep(solutionStep)
  }
}
