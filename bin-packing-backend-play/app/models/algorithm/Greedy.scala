package models.algorithm

import scala.annotation.tailrec

class Greedy[Candidate, Solution](selectionHandler: SelectionHandler[Candidate, Solution]) {

  def run(afterStep: (Solution, Int, Boolean) => Unit = (_, _, _) => {}): Solution = {

    @tailrec
    def runRecursively(
      currentSolution: Solution,
      remainingObjectsToPlace: Iterable[Candidate],
      currentStep: Int
    ): Solution =
      remainingObjectsToPlace match {
        case candidates if candidates.isEmpty =>
          afterStep(currentSolution, currentStep, true)
          currentSolution
        case candidates =>
          val (nextCandidate, remainingCandidates) = selectionHandler.selectNextCandidate(candidates)
          val nextSolution = selectionHandler.placeCandidateInSolution(nextCandidate, currentSolution)
          afterStep(nextSolution, currentStep, false)
          runRecursively(nextSolution, remainingCandidates, currentStep + 1)
      }

    runRecursively(selectionHandler.startSolution, selectionHandler.candidates, 1)
  }

}

trait SelectionHandler[Candidate, Solution] {

  val startSolution: Solution
  val candidates: Iterable[Candidate]

  def selectNextCandidate(candidates: Iterable[Candidate]): (Candidate, Iterable[Candidate])

  def placeCandidateInSolution(candidate: Candidate, solution: Solution): Solution
}
