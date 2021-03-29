package models.problem.binpacking.localsearch.neighborhood

import models.problem.binpacking.solution.BoxClosingTopLeftFirstBinPackingSolution
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Rectangle

import scala.collection.View

class RectanglePermutationNeighborhood(
  val boxLength: Int,
  val targetBoxFillGrade: Double,
  val consideredCandidatesAreaOvershootFactor: Double,
  val consideredCandidatesAreaMaximumRelativeSize: Double
) {

  private val targetFillArea = targetBoxFillGrade * boxLength * boxLength

  def createRectanglePermutationNeighborhood(
    solution: BoxClosingTopLeftFirstBinPackingSolution
  ): View[BoxClosingTopLeftFirstBinPackingSolution] = {
    val rectanglePermutationPerBox = solution.rectangles.groupBy(solution.placement(_).box.id)
    solution.getPlacementsPerBox.toSeq
      .sortBy(_._1)
      .dropRight(1)
      .view
      .collect {
        case (boxId, placement) if getOverallRectangleArea(placement) < targetFillArea =>
          pullUpRectangles(
            solution.prefixWithOpenLastBox(boxId),
            boxId,
            boxLength * boxLength - getOverallRectangleArea(placement),
            rectanglePermutationPerBox.toSeq
              .sortBy(_._1)
              .collect {
                case (id, rectangles) if id > boxId => rectangles
              }
              .flatten
          )
      }
      .flatten
  }

  private def pullUpRectangles(
    solutionPrefix: BoxClosingTopLeftFirstBinPackingSolution,
    boxId: Int,
    emptyBoxArea: Int,
    remainingPermutation: Seq[Rectangle]
  ): Option[BoxClosingTopLeftFirstBinPackingSolution] = {
    val pullUpCandidateIndexes = getPromisingPullUpCandidateIndexes(remainingPermutation, emptyBoxArea)
    val (solutionWithBoxFilled, usedIndexes) = pullUpCandidateIndexes.foldLeft((solutionPrefix, Seq.empty[Int])) {
      case ((updatedSolution, usedIndexes), index) =>
        val rectangle = remainingPermutation(index)
        updatedSolution.placeTopLeftFirstInSpecificBox(rectangle, boxId) match {
          case Some(solution) => (solution, usedIndexes.appended(index))
          case None => (updatedSolution, usedIndexes)
        }
    }
    if (usedIndexes.isEmpty) {
      Option.empty[BoxClosingTopLeftFirstBinPackingSolution]
    } else {
      val remainingPermutationWithoutPulledUpCandidates = remainingPermutation.zipWithIndex.collect {
        case (rectangle, index) if !usedIndexes.contains(index) => rectangle
      }
      val solutionWithRemainingPermutationPlaced =
        remainingPermutationWithoutPulledUpCandidates.foldLeft(solutionWithBoxFilled) {
          case (updatedSolution, rectangle) => updatedSolution.placeTopLeftFirst(rectangle)
        }
      Option(solutionWithRemainingPermutationPlaced)
    }
  }

  private def getPromisingPullUpCandidateIndexes(rectangles: Seq[Rectangle], areaToFill: Int): Seq[Int] = {
    val (candidateIndexes, _) = rectangles.zipWithIndex.foldLeft((Seq.empty[Int], 0.0)) {
      case ((indexes, candidatesOverallArea), _)
          if candidatesOverallArea >= consideredCandidatesAreaOvershootFactor * areaToFill =>
        (indexes, candidatesOverallArea)
      case ((indexes, candidatesOverallArea), (rectangle, _))
          if rectangle.getArea > Math.max(consideredCandidatesAreaMaximumRelativeSize * areaToFill, 1.0) =>
        (indexes, candidatesOverallArea)
      case ((indexes, candidatesOverallArea), (rectangle, index)) =>
        (indexes.appended(index), candidatesOverallArea + rectangle.getArea)
    }
    candidateIndexes
  }

  private def getOverallRectangleArea(placement: Map[Rectangle, Coordinates]): Int = {
    placement.keys.toSeq.map(_.getArea).sum
  }

}
