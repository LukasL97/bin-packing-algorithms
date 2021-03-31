package models.problem.binpacking.localsearch.neighborhood

import models.problem.binpacking.solution.Overlapping
import models.problem.binpacking.solution.OverlappingTopLeftFirstBinPackingSolution
import models.problem.binpacking.solution.Rectangle

import scala.collection.View

class ExceededOverlapOutsourcingNeighborhood(
  val boxLength: Int
) {

  def createExceededOverlapOutsourcingNeighborhood(
    solution: OverlappingTopLeftFirstBinPackingSolution,
    maxOverlap: Double
  ): View[OverlappingTopLeftFirstBinPackingSolution] = {
    val exceededOverlappings = solution.getExceededOverlappings(maxOverlap)
    if (exceededOverlappings.isEmpty) {
      View.empty[OverlappingTopLeftFirstBinPackingSolution]
    } else {
      Seq(
        resolveExceededOverlappingsByOutsourcingIntoNewBoxes(
          solution,
          maxOverlap,
          exceededOverlappings
        )
      ).view
    }
  }

  private def resolveExceededOverlappingsByOutsourcingIntoNewBoxes(
    solution: OverlappingTopLeftFirstBinPackingSolution,
    maxOverlap: Double,
    exceededOverlappings: Map[Int, Set[Overlapping]]
  ): OverlappingTopLeftFirstBinPackingSolution = {
    val rectanglesToOutsource = exceededOverlappings.map {
      case (boxId, boxOverlappings) => boxId -> collectRectanglesToOutsource(boxOverlappings.toSeq)
    }
    val solutionWithOutsourcedRectanglesRemoved = rectanglesToOutsource.foldLeft(solution) {
      case (updatedSolution, (boxId, rectangles)) =>
        rectangles.foldLeft(updatedSolution) {
          case (innerUpdatedSolution, rectangle) => innerUpdatedSolution.removeRectangleFromBox(rectangle.id, boxId)
        }
    }
    val outsourcedRectanglesSolutionSuffix = OverlappingTopLeftFirstBinPackingSolution.apply(
      rectanglesToOutsource.values.flatten.toSeq,
      boxLength,
      maxOverlap
    )
    solutionWithOutsourcedRectanglesRemoved.appended(outsourcedRectanglesSolutionSuffix)
  }

  private def collectRectanglesToOutsource(
    overlappings: Seq[Overlapping]
  ): Seq[Rectangle] = {
    val allRectangles = (overlappings.map(_.rectangleA) ++ overlappings.map(_.rectangleB))
      .groupBy(rectangle => rectangle)
      .map {
        case (rectangle, occurrences) => rectangle -> occurrences.size
      }
    val allRectanglesOrderedByOccurrence = allRectangles.toSeq.sortBy(_._2).reverse.map(_._1)
    allRectanglesOrderedByOccurrence.foldLeft(Seq.empty[Rectangle]) {
      case (collectedRectangles, _) if rectanglesCoverAllOverlappings(collectedRectangles, overlappings) =>
        collectedRectangles
      case (collectedRectangles, rectangle) => collectedRectangles.appended(rectangle)
    }
  }

  private def rectanglesCoverAllOverlappings(
    rectangles: Seq[Rectangle],
    overlappings: Seq[Overlapping]
  ): Boolean = {
    overlappings.forall {
      case Overlapping(rectangleA, _, rectangleB, _, _) =>
        rectangles.contains(rectangleA) || rectangles.contains(rectangleB)
    }
  }

}
