package models.algorithm

import metrics.Metrics

import scala.annotation.tailrec
import scala.collection.View

class LocalSearch[Solution](solutionHandler: SolutionHandler[Solution]) extends Metrics {

  sealed trait StepResult
  case class Ongoing(solution: Solution) extends StepResult
  case class Finished(solution: Solution) extends StepResult

  def run(maxSteps: Int, timeLimit: Option[Int], afterStep: (Solution, Int, Boolean) => Unit): Solution = {
    val startTime = System.currentTimeMillis()

    @tailrec
    def runRecursively(currentSolution: Solution, currentStep: Int): Solution = {
      if (currentStep <= maxSteps && !timeIsUp(startTime, timeLimit)) {
        step(currentSolution, currentStep) match {
          case Ongoing(solution) =>
            afterStep(solution, currentStep, false)
            runRecursively(solution, currentStep + 1)
          case Finished(solution) =>
            afterStep(solution, currentStep, true)
            solution
        }
      } else {
        afterStep(currentSolution, currentStep, true)
        currentSolution
      }
    }

    runRecursively(solutionHandler.startSolution, 1)
  }

  private def step(currentSolution: Solution, step: Int): StepResult = {
    withTimer("local-search-run-step", "step" -> step.toString) {
      val currentSolutionResult = solutionHandler.evaluate(currentSolution, step)
      val neighborhood = solutionHandler.getNeighborhood(currentSolution, step)
      neighborhood.find(solutionHandler.evaluate(_, step) < currentSolutionResult) match {
        case Some(solution) => Ongoing(solution)
        case None => stagnation(currentSolution)
      }
    }
  }

  private def stagnation(solution: Solution): StepResult = {
    if (solutionHandler.stopOnStagnation(solution)) {
      Finished(solution)
    } else {
      Ongoing(solution)
    }
  }

  private def timeIsUp(startTime: Long, timeLimit: Option[Int]): Boolean = {
    timeLimit.exists(millis => System.currentTimeMillis() - startTime >= millis)
  }

}

trait SolutionHandler[Solution] {

  val startSolution: Solution

  def getNeighborhood(solution: Solution, step: Int): View[Solution]

  def evaluate(solution: Solution, step: Int): Score

  def stopOnStagnation(solution: Solution): Boolean = true
}

trait Score extends Ordered[Score]

case class OneDimensionalScore(
  value: Double
) extends Score {
  override def compare(that: Score): Int = value.compare(that.asInstanceOf[OneDimensionalScore].value)
}
