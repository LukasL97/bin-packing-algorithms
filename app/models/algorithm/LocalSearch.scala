package models.algorithm

trait LocalSearch[A <: Solution] {

  implicit def solutionToA(solution: Solution): A = solution.asInstanceOf[A]

  sealed trait StepResult
  case class Improvement(solution: A) extends StepResult
  case class Stagnation(solution: A) extends StepResult

  val solutionHandler: SolutionHandler[A]

  def startSolution: A = solutionHandler.createArbitraryFeasibleSolution()

  def step(currentSolution: A): StepResult = {
    val currentSolutionResult = solutionHandler.evaluate(currentSolution)
    val neighborhood = solutionHandler.getNeighborhood(currentSolution)
    neighborhood.find(solutionHandler.evaluate(_) < currentSolutionResult) match {
      case Some(solution) => Improvement(solution)
      case None => Stagnation(currentSolution)
    }
  }

  def run(maxSteps: Int, beforeStep: A => Unit): A = {
    def runRecursively(currentSolution: A, remainingSteps: Int): A = if (remainingSteps > 0) {
      beforeStep(currentSolution)
      step(currentSolution) match {
        case Improvement(solution) => runRecursively(solution, remainingSteps - 1)
        case Stagnation(solution) => solution
      }
    } else {
      currentSolution
    }
    runRecursively(startSolution, maxSteps)
  }

}

trait Solution

trait SolutionHandler[A <: Solution] {

  def createArbitraryFeasibleSolution(): A

  def getNeighborhood(solution: A): Set[A]

  def evaluate(solution: A): Double
}
