package models.problem.binpacking.localsearch.neighborhood

import models.problem.binpacking.SimpleBinPackingSolution
import models.problem.binpacking.BinPackingSolutionValidator
import models.problem.binpacking.Coordinates
import models.problem.binpacking.Placing
import models.problem.binpacking.Rectangle
import models.problem.binpacking.utils.BinPackingSolutionUtil

import scala.collection.mutable

trait GeometricShiftNeighborhood extends BinPackingSolutionValidator with BinPackingSolutionUtil {

  def createShiftedSolutions(
    solution: SimpleBinPackingSolution,
    direction: Direction,
    stepSize: Int,
    ensureFeasibility: Boolean
  ): Set[SimpleBinPackingSolution] = {
    solution.placement.flatMap {
      case (rectangle, placing) =>
        shiftRectangleInSolution(solution, rectangle, placing, direction, stepSize, ensureFeasibility)
    }.toSet
  }

  def createMaximallyShiftedSolutions(
    solution: SimpleBinPackingSolution,
    direction: Direction,
  ): Set[SimpleBinPackingSolution] = {

    def shiftRectangleInSolutionUntilHittingBarrier(
      originalSolution: SimpleBinPackingSolution,
      rectangle: Rectangle,
      placing: Placing
    ): Option[SimpleBinPackingSolution] = {
      var stepSize = 1
      var solutions: Seq[Option[SimpleBinPackingSolution]] =
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
    solution: SimpleBinPackingSolution,
    rectangle: Rectangle,
    placing: Placing,
    direction: Direction,
    stepSize: Int,
    ensureFeasibility: Boolean
  ): Option[SimpleBinPackingSolution] = {
    val shiftedSolution = SimpleBinPackingSolution(
      solution.placement.updated(
        rectangle,
        Placing(placing.box, shift(placing.coordinates, direction, stepSize))
      )
    )
    if (ensureFeasibility && !isFeasibleInSingleBox(
          getPlacementInSingleBox(shiftedSolution, placing.box.id),
          placing.box.length
        )) {
      Option.empty[SimpleBinPackingSolution]
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
