package models.problem.rectangles

import models.algorithm.{Solution, SolutionHandler}

import scala.util.Random

trait RectanglesPlacement {

  val boxLength: Int
  val numRectangles: Int
  val rectangleWidthRange: (Int, Int)
  val rectangleHeightRange: (Int, Int)

  val boxes: Seq[Box] = (1 to numRectangles).map(index => Box(index, boxLength, boxLength))

  private val (rectangleWidthMin, rectangleWidthMax) = rectangleWidthRange
  private val (rectangleHeightMin, rectangleHeightMax) = rectangleHeightRange

  val rectangles: Seq[Rectangle] = (1 to numRectangles).map(index => Rectangle(
    index,
    rectangleWidthMin + Random.nextInt(rectangleWidthMax - rectangleWidthMin + 1),
    rectangleHeightMin + Random.nextInt(rectangleHeightMax - rectangleHeightMin + 1)
  ))

}

case class RectanglesPlacementSolution (
  placement: Map[Rectangle, (Box, (Int, Int))]
) extends Solution

trait RectanglesPlacementSolutionHandler extends SolutionHandler[RectanglesPlacementSolution] {

  def isFeasible(solution: RectanglesPlacementSolution): Boolean = {
    allRectanglesInBox(solution) && allRectanglesDisjunctive(solution)
  }

  def allRectanglesDisjunctive(solution: RectanglesPlacementSolution): Boolean = {
    solution.placement.groupBy {
      case (rectangle, (box, (x, y))) => box
    }.map {
      case (box, placement) => isFeasibleInSingleBox(placement.map {
        case (rectangle, (box, (x, y))) => (rectangle, (x, y))
      })
    }.forall(identity)
  }

  def isFeasibleInSingleBox(placement: Map[Rectangle, (Int, Int)]): Boolean = buildPairs(placement).map {
    case (placingA, placingB) => disjunctive(placingA, placingB)
  }.forall(identity)

  def buildPairs(placement: Map[Rectangle, (Int, Int)]): Seq[((Rectangle, (Int, Int)), (Rectangle, (Int, Int)))] = {
    val placementSeq = placement.toSeq
    placementSeq.zipWithIndex.flatMap {
      case ((rectangleA, (xA, yA)), index) => placementSeq.take(index).map {
        case (rectangleB, (xB, yB)) => ((rectangleA, (xA, yA)), (rectangleB, (xB, yB)))
      }
    }
  }

  def disjunctive(placingA: (Rectangle, (Int, Int)), placingB: (Rectangle, (Int, Int))): Boolean = {
    val (rectangleA, (xA, yA)) = placingA
    val (rectangleB, (xB, yB)) = placingB
    val aLeftOfB = xA + rectangleA.width <= xB
    val aRightOfB = xA >= xB + rectangleB.width
    val aAboveB = yA + rectangleA.height <= yB
    val aBelowB = yA >= yB + rectangleB.height
    aLeftOfB || aRightOfB || aAboveB || aBelowB
  }

  def allRectanglesInBox(solution: RectanglesPlacementSolution): Boolean = solution.placement.map {
    case (rectangle, (box, (x, y))) =>
      0 <= x && x + rectangle.width <= box.width && 0 <= y && y + rectangle.height <= box.height
  }.forall(identity)

}

case class Rectangle(
  id: Int,
  width: Int,
  height: Int
)

case class Box(
  id: Int,
  width: Int,
  height: Int
)
