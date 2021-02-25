package models.algorithm

import scala.annotation.tailrec

class Greedy[Candidate, Solution](selectionHandler: SelectionHandler[Candidate, Solution]) {

  def run(): Solution = {

    @tailrec
    def runRecursively(currentSolution: Solution, remainingObjectsToPlace: Iterable[Candidate]): Solution =
      remainingObjectsToPlace match {
        case candidates if candidates.isEmpty => currentSolution
        case candidates =>
          val (nextCandidate, remainingCandidates) = selectionHandler.selectNextCandidate(candidates)
          runRecursively(selectionHandler.placeCandidateInSolution(nextCandidate, currentSolution), remainingCandidates)
      }

    runRecursively(selectionHandler.startSolution, selectionHandler.candidates)
  }

}

trait SelectionHandler[Candidate, Solution] {

  val startSolution: Solution
  val candidates: Iterable[Candidate]

  def selectNextCandidate(candidates: Iterable[Candidate]): (Candidate, Iterable[Candidate])

  def placeCandidateInSolution(candidate: Candidate, solution: Solution): Solution
}
