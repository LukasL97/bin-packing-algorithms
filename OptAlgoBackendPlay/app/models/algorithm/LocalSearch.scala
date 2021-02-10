package models.algorithm

trait LocalSearch[A <: Solution] {

  implicit def solutionToA(solution: Solution): A = solution.asInstanceOf[A]

  sealed trait StepResult
  case class Improvement(solution: A) extends StepResult
  case class Stagnation(solution: A) extends StepResult

  val solutionHandler: SolutionHandler[A]

  lazy val startSolution: A = solutionHandler.createArbitraryFeasibleSolution()

  def step(currentSolution: A): StepResult = {
    val currentSolutionResult = solutionHandler.evaluate(currentSolution)
    val neighborhood = solutionHandler.getNeighborhood(currentSolution)
    neighborhood.find(solutionHandler.evaluate(_) < currentSolutionResult) match {
      case Some(solution) => Improvement(solution)
      case None => Stagnation(currentSolution)
    }
  }

  def run(maxSteps: Int, afterStep: (A, Int, Boolean) => Unit): A = {
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
    runRecursively(startSolution, 1)
  }

}

trait Solution

trait SolutionHandler[A <: Solution] {

  def createArbitraryFeasibleSolution(): A

  def getNeighborhood(solution: A): Set[A]

  def evaluate(solution: A): BigDecimal
}
