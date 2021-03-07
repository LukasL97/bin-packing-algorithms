package models.problem.binpacking

trait BinPackingSolutionValidator {

  def isFeasible(solution: BinPackingSolution): Boolean = {
    allRectanglesInBox(solution) && allRectanglesDisjunctive(solution)
  }

  def allRectanglesInBox(solution: BinPackingSolution): Boolean =
    solution.placement.map {
      case (rectangle, Placing(box, Coordinates(x, y))) =>
        0 <= x && x + rectangle.width <= box.length && 0 <= y && y + rectangle.height <= box.length
    }.forall(identity)

  private def allRectanglesDisjunctive(solution: BinPackingSolution): Boolean = {
    solution.placement.groupBy {
      case (rectangle, placing) => placing.box
    }.map {
      case (box, placement) =>
        allRectanglesDisjunctiveInSingleBox(placement.map {
          case (rectangle, placing) => (rectangle, placing.coordinates)
        })
    }.forall(identity)
  }

  def isFeasibleInSingleBox(placement: Map[Rectangle, Coordinates], boxLength: Int): Boolean = {
    allRectanglesInBoundsForSingleBox(placement, boxLength) && allRectanglesDisjunctiveInSingleBox(placement)
  }

  def allRectanglesInBoundsForSingleBox(placement: Map[Rectangle, Coordinates], boxLength: Int): Boolean = {
    placement.map {
      case (rectangle, Coordinates(x, y)) =>
        0 <= x && x + rectangle.width <= boxLength && 0 <= y && y + rectangle.height <= boxLength
    }.forall(identity)
  }

  def allRectanglesDisjunctiveInSingleBox(placement: Map[Rectangle, Coordinates]): Boolean =
    buildPairs(placement).map {
      case (placingA, placingB) => disjunctive(placingA, placingB)
    }.forall(identity)

  private def buildPairs(
    placement: Map[Rectangle, Coordinates]
  ): Seq[((Rectangle, Coordinates), (Rectangle, Coordinates))] = {
    val placementSeq = placement.toSeq
    placementSeq.zipWithIndex.flatMap {
      case ((rectangleA, coordinatesA), index) =>
        placementSeq.take(index).map {
          case (rectangleB, coordinatesB) => ((rectangleA, coordinatesA), (rectangleB, coordinatesB))
        }
    }
  }

  private def disjunctive(placingA: (Rectangle, Coordinates), placingB: (Rectangle, Coordinates)): Boolean = {
    val (rectangleA, coordinatesA) = placingA
    val (rectangleB, coordinatesB) = placingB
    val aLeftOfB = coordinatesA.x + rectangleA.width <= coordinatesB.x
    val aRightOfB = coordinatesA.x >= coordinatesB.x + rectangleB.width
    val aAboveB = coordinatesA.y + rectangleA.height <= coordinatesB.y
    val aBelowB = coordinatesA.y >= coordinatesB.y + rectangleB.height
    aLeftOfB || aRightOfB || aAboveB || aBelowB
  }
}
