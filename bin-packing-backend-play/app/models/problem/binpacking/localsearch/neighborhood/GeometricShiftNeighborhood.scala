package models.problem.binpacking.localsearch.neighborhood

import metrics.Metrics
import models.problem.binpacking.BinPackingSolutionValidator
import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle

import scala.collection.View

class GeometricShiftNeighborhood(
  val boxLength: Int
) extends BinPackingSolutionValidator with Metrics {

  def createShiftedSolutions(
    solution: BinPackingSolution,
    direction: Direction,
    stepSize: Int,
    allowOverlap: Boolean
  ): View[BinPackingSolution] = {
    solution.placement.view.flatMap {
      case (rectangle, placing) =>
        shiftRectangleInSolution(solution, rectangle, placing, direction, stepSize, allowOverlap)
    }
  }

  def createMaximallyShiftedSolutions(
    solution: BinPackingSolution,
    direction: Direction
  ): View[BinPackingSolution] = {
    solution.placement.view.flatMap {
      case (rectangle, placing) =>
        shiftRectangleInSolutionUntilHittingBarrier(solution, rectangle, placing, direction)
    }
  }

  def createEntireBoxMaximallyShiftedSolutions(
    solution: BinPackingSolution,
    direction: Direction
  ): View[BinPackingSolution] = {
    solution.getPlacementsPerBox.view.flatMap {
      case (boxId, placement) => withTimer("entire-box-maximally-shifted-neighborhood") {
        val placementWithBox = placement.map {
          case (rectangle, coordinates) => rectangle -> Placing(Box(boxId, boxLength), coordinates)
        }
        shiftAllRectanglesInBoxUntilHittingBarrier(solution, placementWithBox, direction)
      }
    }
  }

  private def shiftAllRectanglesInBoxUntilHittingBarrier(
    originalSolution: BinPackingSolution,
    boxPlacement: Map[Rectangle, Placing],
    direction: Direction
  ): Option[BinPackingSolution] = {
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
      case (_, false) => Option.empty[BinPackingSolution]
      case (solution, true) => Option(solution)
    }
  }

  private def shiftRectangleInSolutionUntilHittingBarrier(
    originalSolution: BinPackingSolution,
    rectangle: Rectangle,
    placing: Placing,
    direction: Direction
  ): Option[BinPackingSolution] = {
    var stepSize = 1
    var solutions: Seq[Option[BinPackingSolution]] =
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
    solution: BinPackingSolution,
    rectangle: Rectangle,
    placing: Placing,
    direction: Direction,
    stepSize: Int,
    allowOverlap: Boolean
  ): Option[BinPackingSolution] = {
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
      Option.empty[BinPackingSolution]
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
