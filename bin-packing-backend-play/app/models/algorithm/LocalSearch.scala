package models.algorithm

import scala.annotation.tailrec
import scala.collection.View

class LocalSearch[Solution](solutionHandler: SolutionHandler[Solution]) {

  sealed trait StepResult
  case class Ongoing(solution: Solution) extends StepResult
  case class Finished(solution: Solution) extends StepResult

  private def step(currentSolution: Solution, stepNumber: Int): StepResult = {
    val currentSolutionResult = solutionHandler.evaluate(currentSolution, stepNumber)
    val neighborhood = solutionHandler.getNeighborhood(currentSolution)
    neighborhood.find(solutionHandler.evaluate(_, stepNumber) < currentSolutionResult) match {
      case Some(solution) => Ongoing(solution)
      case None => stagnation(currentSolution)
    }
  }

  private def stagnation(solution: Solution): StepResult = {
    if (solutionHandler.stopOnStagnation(solution)) {
      Finished(solution)
    } else {
      Ongoing(solution)
    }
  }

  def run(maxSteps: Int, afterStep: (Solution, Int, Boolean) => Unit): Solution = {
    @tailrec
    def runRecursively(currentSolution: Solution, currentStep: Int): Solution = {
      if (currentStep <= maxSteps) {
        step(currentSolution, currentStep) match {
          case Ongoing(solution) =>
            afterStep(solution, currentStep, false)
            runRecursively(solution, currentStep + 1)
          case Finished(solution) =>
            afterStep(solution, currentStep, true)
            solution
        }
      } else {
        currentSolution
      }
    }

    runRecursively(solutionHandler.startSolution, 1)
  }

}

trait SolutionHandler[Solution] {

  val startSolution: Solution

  def getNeighborhood(solution: Solution): View[Solution]

  def evaluate(solution: Solution, step: Int): Score

  def stopOnStagnation(solution: Solution): Boolean = true
}

trait Score extends Ordered[Score]

case class OneDimensionalScore(
  value: BigDecimal
) extends Score {
  override def compare(that: Score): Int = value.compare(that.asInstanceOf[OneDimensionalScore].value)
}
