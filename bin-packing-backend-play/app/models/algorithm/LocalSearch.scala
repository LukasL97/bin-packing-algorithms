package models.algorithm

import scala.annotation.tailrec

class LocalSearch[A <: Solution](solutionHandler: SolutionHandler[A]) {

  sealed trait StepResult
  case class Improvement(solution: A) extends StepResult
  case class Stagnation(solution: A) extends StepResult

  def step(currentSolution: A): StepResult = {
    val currentSolutionResult = solutionHandler.evaluate(currentSolution)
    val neighborhood = solutionHandler.getNeighborhood(currentSolution)
    neighborhood.find(solutionHandler.evaluate(_) < currentSolutionResult) match {
      case Some(solution) => Improvement(solution)
      case None => Stagnation(currentSolution)
    }
  }

  def run(maxSteps: Int, afterStep: (A, Int, Boolean) => Unit): A = {
    @tailrec
    def runRecursively(currentSolution: A, currentStep: Int): A = if (currentStep <= maxSteps) {
      step(currentSolution) match {
        case Improvement(solution) =>
          afterStep(solution, currentStep, false)
          runRecursively(solution, currentStep + 1)
        case Stagnation(solution) =>
          afterStep(solution, currentStep, true)
          solution
      }
    } else {
      currentSolution
    }
    runRecursively(solutionHandler.startSolution, 1)
  }

}

trait Solution

trait SolutionHandler[A <: Solution] {

  val startSolution: A

  def getNeighborhood(solution: A): Set[A]

  def evaluate(solution: A): BigDecimal
}
