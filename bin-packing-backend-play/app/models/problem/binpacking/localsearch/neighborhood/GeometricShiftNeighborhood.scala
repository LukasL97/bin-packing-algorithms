package models.problem.binpacking.localsearch.neighborhood

import models.problem.binpacking.BinPackingSolution
import models.problem.binpacking.BinPackingSolutionValidator
import models.problem.binpacking.Coordinates
import models.problem.binpacking.Placing
import models.problem.binpacking.Rectangle
import models.problem.binpacking.utils.BinPackingSolutionUtil

import scala.collection.mutable

trait GeometricShiftNeighborhood extends BinPackingSolutionValidator with BinPackingSolutionUtil {

  def createShiftedSolutions(
    solution: BinPackingSolution,
    direction: Direction,
    stepSize: Int,
    ensureFeasibility: Boolean
  ): Set[BinPackingSolution] = {
    solution.placement.flatMap {
      case (rectangle, placing) =>
        shiftRectangleInSolution(solution, rectangle, placing, direction, stepSize, ensureFeasibility)
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
          shiftRectangleInSolution(originalSolution, rectangle, placing, direction, stepSize, ensureFeasibility = true)
        )
      while (solutions.last.isDefined) {
        stepSize += 1
        solutions = solutions.appended(
          shiftRectangleInSolution(originalSolution, rectangle, placing, direction, stepSize, ensureFeasibility = true)
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
    ensureFeasibility: Boolean
  ): Option[BinPackingSolution] = {
    val shiftedSolution = BinPackingSolution(
      solution.placement.updated(
        rectangle,
        Placing(placing.box, shift(placing.coordinates, direction, stepSize))
      )
    )
    if (ensureFeasibility && !isFeasibleInSingleBox(
          getPlacementInSingleBox(shiftedSolution, placing.box.id),
          placing.box.length
        )) {
      Option.empty[BinPackingSolution]
    } else {
      Option(shiftedSolution)
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
