package models.problem.binpacking.localsearch.neighborhood

import metrics.Metrics
import models.problem.binpacking.BinPackingSolutionValidator
import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.transformation.RectanglePlacingUpdateSupport
import models.problem.binpacking.solution.update.RectanglesChanged
import models.problem.binpacking.solution.update.UpdateStoringSupport

import scala.collection.View

class GeometricShiftNeighborhood[A <: RectanglePlacingUpdateSupport[A] with UpdateStoringSupport[A]](
  val boxLength: Int
) extends BinPackingSolutionValidator with Metrics {

  def createShiftedSolutions(
    solution: A,
    direction: Direction,
    stepSize: Int,
    allowOverlap: Boolean
  ): View[A] = {
    solution.placement.view.flatMap {
      case (rectangle, placing) =>
        shiftRectangleInSolution(solution, rectangle, placing, direction, stepSize, allowOverlap)
          .map(_.setUpdate(RectanglesChanged(Set(rectangle.id))))
    }
  }

  def createMaximallyShiftedSolutions(
    solution: A,
    direction: Direction
  ): View[A] = {
    solution.placement.view.flatMap {
      case (rectangle, placing) =>
        shiftRectangleInSolutionUntilHittingBarrier(solution, rectangle, placing, direction)
          .map(_.setUpdate(RectanglesChanged(Set(rectangle.id))))
    }
  }

  def createEntireBoxMaximallyShiftedSolutions(
    solution: A,
    direction: Direction
  ): View[A] = {
    solution.getPlacementsPerBox.view.flatMap {
      case (boxId, placement) =>
        withTimer("entire-box-maximally-shifted-neighborhood") {
          val placementWithBox = placement.map {
            case (rectangle, coordinates) => rectangle -> Placing(Box(boxId, boxLength), coordinates)
          }
          shiftAllRectanglesInBoxUntilHittingBarrier(solution, placementWithBox, direction)
            .map(_.setUpdate(RectanglesChanged(placementWithBox.keys.map(_.id).toSet)))
        }
    }
  }

  private def shiftAllRectanglesInBoxUntilHittingBarrier(
    originalSolution: A,
    boxPlacement: Map[Rectangle, Placing],
    direction: Direction
  ): Option[A] = {
    implicit val placementOrdering: Ordering[(Rectangle, Placing)] = {
      (placingA: (Rectangle, Placing), placingB: (Rectangle, Placing)) =>
        direction match {
          case Left => placingA._2.coordinates.x - placingB._2.coordinates.x
          case Right => placingB._2.coordinates.x - placingA._2.coordinates.x
          case Up => placingA._2.coordinates.y - placingB._2.coordinates.y
          case Down => placingB._2.coordinates.y - placingA._2.coordinates.y
        }
    }
    val sortedPlacement = boxPlacement.toSeq.sorted
    val updatedSolutionWithChangedFlag = sortedPlacement.foldLeft((originalSolution, false)) {
      case ((solution, hasChanged), (rectangle, placing)) =>
        val updatedSolution = shiftRectangleInSolutionUntilHittingBarrier(solution, rectangle, placing, direction)
        (updatedSolution.getOrElse(solution), hasChanged || updatedSolution.isDefined)
    }
    updatedSolutionWithChangedFlag match {
      case (_, false) => Option.empty[A]
      case (solution, true) => Option(solution)
    }
  }

  private def shiftRectangleInSolutionUntilHittingBarrier(
    originalSolution: A,
    rectangle: Rectangle,
    placing: Placing,
    direction: Direction
  ): Option[A] = {
    var stepSize = 1
    var solutions: Seq[Option[A]] =
      Seq(
        shiftRectangleInSolution(originalSolution, rectangle, placing, direction, stepSize, allowOverlap = false)
      )
    while (solutions.last.isDefined) {
      stepSize += 1
      solutions = solutions.appended(
        shiftRectangleInSolution(originalSolution, rectangle, placing, direction, stepSize, allowOverlap = false)
      )
    }
    solutions.flatten.lastOption
  }

  private def shiftRectangleInSolution(
    solution: A,
    rectangle: Rectangle,
    placing: Placing,
    direction: Direction,
    stepSize: Int,
    allowOverlap: Boolean
  ): Option[A] = {
    val newCoordinates = shift(placing.coordinates, direction, stepSize)
    if ((allowOverlap && inBounds(rectangle, newCoordinates, boxLength)) || validateNewPlacingInSingleBox(
          rectangle,
          newCoordinates,
          solution.getPlacementInSingleBox(placing.box.id).removed(rectangle),
          boxLength
        )) {
      Option(
        solution.updated(rectangle, Placing(placing.box, newCoordinates))
      )
    } else {
      Option.empty[A]
    }
  }

  private def shift(coordinates: Coordinates, direction: Direction, stepSize: Int): Coordinates = {
    coordinates match {
      case Coordinates(x, y) =>
        direction match {
          case Left => Coordinates(x - stepSize, y)
          case Right => Coordinates(x + stepSize, y)
          case Up => Coordinates(x, y - stepSize)
          case Down => Coordinates(x, y + stepSize)
        }
    }
  }

}

sealed trait Direction
case object Left extends Direction
case object Right extends Direction
case object Up extends Direction
case object Down extends Direction
