package models.problem.binpacking.localsearch.neighborhood

import models.problem.binpacking.BinPackingSolutionValidator
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.BinPackingSolution

trait GeometricShiftNeighborhood extends BinPackingSolutionValidator {

  def createShiftedSolutions(
    solution: BinPackingSolution,
    direction: Direction,
    stepSize: Int,
    allowOverlap: Boolean
  ): Set[BinPackingSolution] = {
    solution.placement.flatMap {
      case (rectangle, placing) =>
        shiftRectangleInSolution(solution, rectangle, placing, direction, stepSize, allowOverlap)
    }.toSet
  }

  def createMaximallyShiftedSolutions(
    solution: BinPackingSolution,
    direction: Direction,
  ): Set[BinPackingSolution] = {

    def shiftRectangleInSolutionUntilHittingBarrier(
      originalSolution: BinPackingSolution,
      rectangle: Rectangle,
      placing: Placing
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

    solution.placement.flatMap {
      case (rectangle, placing) =>
        shiftRectangleInSolutionUntilHittingBarrier(solution, rectangle, placing)
    }.toSet
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
    if ((allowOverlap && inBounds(rectangle, newCoordinates, placing.box.length)) || validateNewPlacingInSingleBox(
          rectangle,
          newCoordinates,
          solution.getPlacementInSingleBox(placing.box.id).removed(rectangle),
          placing.box.length
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
