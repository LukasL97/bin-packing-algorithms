package actors.dumpers.combining

import actors.BinPackingSolutionStep
import akka.actor.Actor

class CombiningSolutionStepDumper(
  val processor: CombiningSolutionStepDumperProcessor
) extends Actor {
  override def receive: Receive = {
    case solutionStep: BinPackingSolutionStep => processor.process(solutionStep)
  }
}
