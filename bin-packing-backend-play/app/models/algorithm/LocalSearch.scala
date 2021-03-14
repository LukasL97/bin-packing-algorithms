package models.algorithm

import scala.annotation.tailrec

class LocalSearch[Solution](solutionHandler: SolutionHandler[Solution]) {

  sealed trait StepResult
  case class Improvement(solution: Solution) extends StepResult
  case class Stagnation(solution: Solution) extends StepResult

  def step(currentSolution: Solution, stepNumber: Int): StepResult = {
    val currentSolutionResult = solutionHandler.evaluate(currentSolution, stepNumber)
    val neighborhood = solutionHandler.getNeighborhood(currentSolution)
    neighborhood.find(solutionHandler.evaluate(_, stepNumber) < currentSolutionResult) match {
      case Some(solution) => Improvement(solution)
      case None => Stagnation(currentSolution)
    }
  }

  def run(maxSteps: Int, afterStep: (Solution, Int, Boolean) => Unit): Solution = {
    @tailrec
    def runRecursively(currentSolution: Solution, currentStep: Int): Solution =
      if (currentStep <= maxSteps) {
        step(currentSolution, currentStep) match {
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

trait SolutionHandler[Solution] {

  val startSolution: Solution

  def getNeighborhood(solution: Solution): Set[Solution]

  def evaluate(solution: Solution, step: Int): BigDecimal
}
