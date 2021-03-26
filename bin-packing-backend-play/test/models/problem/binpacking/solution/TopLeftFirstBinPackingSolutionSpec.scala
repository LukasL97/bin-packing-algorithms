package models.problem.binpacking.solution

import models.problem.binpacking.BinPackingSolutionValidator
import models.problem.binpacking.BinPackingTopLeftFirstPlacing
import models.problem.binpacking.utils.RectanglesGenerator
import models.problem.binpacking.utils.TopLeftFirstCoordinateOrdering
import org.scalatest.MustMatchers
import org.scalatest.WordSpec

import scala.collection.SortedSet

class TopLeftFirstBinPackingSolutionSpec
    extends WordSpec with MustMatchers with TopLeftFirstCoordinateOrdering with RectanglesGenerator
    with BinPackingSolutionValidator {

  private val boxLength = 10

  "TopLeftFirstBinPackingSolution" should {

    "get new candidate from rectangle bottom left" when {
      val solution = TopLeftFirstBinPackingSolution(boxLength)

      "the nearest right edge is the box left border" in {
        val rectangleBottomLeft = Coordinates(4, 4)
        val placement = Map(
          Rectangle(1, 2, 3) -> Coordinates(0, 0),
          Rectangle(2, 2, 2) -> Coordinates(2, 0),
          Rectangle(3, 2, 6) -> Coordinates(8, 0)
        )
        solution.getNewCandidateFromRectangleBottomLeft(rectangleBottomLeft, placement) mustBe Option(
          Coordinates(0, 4)
        )
      }
      "the nearest right edge is from some other rectangle" in {
        val rectangleBottomLeft = Coordinates(4, 4)
        val placement = Map(
          Rectangle(1, 2, 6) -> Coordinates(0, 0),
          Rectangle(2, 2, 2) -> Coordinates(2, 0),
          Rectangle(3, 2, 6) -> Coordinates(8, 0)
        )
        solution.getNewCandidateFromRectangleBottomLeft(rectangleBottomLeft, placement) mustBe Option(
          Coordinates(2, 4)
        )
      }
      "the nearest right edge contains the rectangle bottom left" in {
        val rectangleBottomLeft = Coordinates(4, 4)
        val placement = Map(
          Rectangle(1, 2, 6) -> Coordinates(0, 0),
          Rectangle(2, 2, 5) -> Coordinates(2, 0),
          Rectangle(3, 2, 6) -> Coordinates(8, 0)
        )
        solution.getNewCandidateFromRectangleBottomLeft(rectangleBottomLeft, placement) mustBe Option(
          rectangleBottomLeft
        )
      }
      "a right edge contains the rectangle bottom left but not in the inside" in {
        val rectangleBottomLeft = Coordinates(4, 4)
        val placement = Map(
          Rectangle(1, 2, 6) -> Coordinates(0, 0),
          Rectangle(2, 2, 4) -> Coordinates(2, 0),
          Rectangle(3, 2, 6) -> Coordinates(8, 0)
        )
        solution.getNewCandidateFromRectangleBottomLeft(rectangleBottomLeft, placement) mustBe Option(
          Coordinates(2, 4)
        )
      }
    }

    "get new candidate from rectangle top right" when {
      val solution = TopLeftFirstBinPackingSolution(boxLength)

      "the nearest bottom edge is the box top border" in {
        val rectangleTopRight = Coordinates(2, 0)
        val placement = Map(
          Rectangle(1, 2, 2) -> Coordinates(0, 0)
        )
        solution.getNewCandidateFromRectangleTopRight(rectangleTopRight, placement) mustBe Option(
          rectangleTopRight
        )
      }
      "the nearest bottom edge is from some other rectangle" in {
        val rectangleTopRight = Coordinates(4, 6)
        val placement = Map(
          Rectangle(1, 5, 2) -> Coordinates(0, 0),
          Rectangle(2, 2, 2) -> Coordinates(0, 2),
          Rectangle(3, 2, 2) -> Coordinates(0, 4)
        )
        solution.getNewCandidateFromRectangleTopRight(rectangleTopRight, placement) mustBe Option(
          Coordinates(4, 2)
        )
      }
    }

    "place a rectangle with top left first strategy" when {
      "placing it into an empty solution" in {
        val solution = TopLeftFirstBinPackingSolution(boxLength)
        val rectangle = Rectangle(1, 3, 4)
        solution.placeTopLeftFirst(rectangle) mustEqual TopLeftFirstBinPackingSolution(
          Map(
            rectangle -> Placing(Box(1, boxLength), Coordinates(0, 0))
          ),
          Map(
            1 -> SortedSet(
              Coordinates(3, 0),
              Coordinates(0, 4)
            )
          ),
          boxLength
        )
      }
      "placing it into a solution with some existing rectangles" in {
        val placement = Map(
          Rectangle(1, 6, 2) -> Placing(Box(1, boxLength), Coordinates(0, 0)),
          Rectangle(2, 3, 5) -> Placing(Box(1, boxLength), Coordinates(0, 2))
        )
        val candidates = Map(
          1 -> SortedSet(
            Coordinates(3, 2),
            Coordinates(6, 0),
            Coordinates(0, 7)
          )
        )
        val solution = TopLeftFirstBinPackingSolution(placement, candidates, boxLength)
        val rectangle = Rectangle(3, 5, 1)
        solution.placeTopLeftFirst(rectangle) mustEqual TopLeftFirstBinPackingSolution(
          placement.updated(rectangle, Placing(Box(1, boxLength), Coordinates(3, 2))),
          Map(
            1 -> SortedSet(
              Coordinates(3, 3),
              Coordinates(6, 0),
              Coordinates(0, 7),
              Coordinates(8, 0),
              Coordinates(6, 3),
              Coordinates(8, 2)
            )
          ),
          boxLength
        )
      }
      "placing it into a solution so that a new candidate appears at its bottom edge" in {
        val placement = Map(
          Rectangle(1, 1, 1) -> Placing(Box(1, boxLength), Coordinates(0, 0)),
          Rectangle(2, 2, 1) -> Placing(Box(1, boxLength), Coordinates(0, 1))
        )
        val candidates = Map(
          1 -> SortedSet(
            Coordinates(1, 0),
            Coordinates(2, 0),
            Coordinates(0, 2)
          )
        )
        val solution = TopLeftFirstBinPackingSolution(placement, candidates, boxLength)
        val rectangle = Rectangle(3, 2, 1)
        solution.placeTopLeftFirst(rectangle) mustEqual TopLeftFirstBinPackingSolution(
          placement.updated(rectangle, Placing(Box(1, boxLength), Coordinates(1, 0))),
          Map(
            1 -> SortedSet(
              Coordinates(0, 2),
              Coordinates(3, 0),
              Coordinates(2, 1)
            )
          ),
          boxLength
        )
      }
    }

    "place a rectangle with top left first strategy in a specific box" when {
      "the rectangle fits into the box" in {
        val boxId = 1
        val placement = Map(
          Rectangle(1, 6, 2) -> Placing(Box(boxId, boxLength), Coordinates(0, 0)),
          Rectangle(2, 3, 5) -> Placing(Box(boxId, boxLength), Coordinates(0, 2))
        )
        val candidates = Map(
          boxId -> SortedSet(
            Coordinates(3, 2),
            Coordinates(6, 0),
            Coordinates(0, 7)
          )
        )
        val solution = TopLeftFirstBinPackingSolution(placement, candidates, boxLength)
        val rectangle = Rectangle(3, 5, 1)
        solution.placeTopLeftFirstInSpecificBox(rectangle, boxId) mustEqual Option(
          TopLeftFirstBinPackingSolution(
            placement.updated(rectangle, Placing(Box(boxId, boxLength), Coordinates(3, 2))),
            Map(
              boxId -> SortedSet(
                Coordinates(3, 3),
                Coordinates(6, 0),
                Coordinates(0, 7),
                Coordinates(8, 0),
                Coordinates(6, 3),
                Coordinates(8, 2)
              )
            ),
            boxLength
          )
        )
      }
      "the rectangle does not fit into the box" in {
        val boxId = 1
        val solution = TopLeftFirstBinPackingSolution(
          Map(
            Rectangle(1, boxLength, boxLength) -> Placing(Box(boxId, boxLength), Coordinates(0, 0))
          ),
          Map(boxId -> SortedSet.empty),
          boxLength
        )
        val rectangle = Rectangle(2, 1, 1)
        solution.placeTopLeftFirstInSpecificBox(rectangle, boxId) must be(None)
      }
      "the rectangle is placed into an empty box" in {
        val boxId = 2
        val solution = TopLeftFirstBinPackingSolution(
          Map(
            Rectangle(1, boxLength, boxLength) -> Placing(Box(1, boxLength), Coordinates(0, 0))
          ),
          Map(1 -> SortedSet.empty),
          boxLength
        )
        val rectangle = Rectangle(2, 1, 1)
        solution.placeTopLeftFirstInSpecificBox(rectangle, boxId) mustEqual Option(
          TopLeftFirstBinPackingSolution(
            Map(
              Rectangle(1, boxLength, boxLength) -> Placing(Box(1, boxLength), Coordinates(0, 0)),
              rectangle -> Placing(Box(boxId, boxLength), Coordinates(0, 0))
            ),
            Map(
              1 -> SortedSet.empty,
              boxId -> SortedSet(Coordinates(0, 1), Coordinates(1, 0))
            ),
            boxLength
          )
        )
      }
    }

    "place rectangles identically to an algorithm that goes through all coordinates" when {
      "given some random rectangles" in withRectangles(100, (1, 4), (1, 4)) { rectangles =>
        val solution = TopLeftFirstBinPackingSolution(boxLength)
        val referenceSolution = SimpleBinPackingSolution.apply(boxLength)
        val referenceAlgorithm = new AllCandidatesTopLeftFirstPlacingAlgorithm(boxLength)
        rectangles.foldLeft((solution, referenceSolution)) {
          case ((solution, referenceSolution), rectangle) =>
            val newSolution = solution.placeTopLeftFirst(rectangle)
            val newReferenceSolution = referenceAlgorithm.placeTopLeftFirst(rectangle, referenceSolution)
            newSolution.placement mustEqual newReferenceSolution.placement
            (newSolution, newReferenceSolution)
        }
      }
    }

    "only maintain candidates that actually allow a minimal rectangle to be placed there" when {
      "given some random rectangles" in withRectangles(100, (1, 4), (1, 4)) { rectangles =>
        val solution = TopLeftFirstBinPackingSolution(boxLength)
        val minimalRectangle = Rectangle(1337, 1, 1)
        rectangles.foldLeft(solution) {
          case (solution, rectangle) =>
            val newSolution = solution.placeTopLeftFirst(rectangle)
            newSolution.getPlacementsPerBox.foreach {
              case (boxId, placement) =>
                newSolution
                  .topLeftCandidates(boxId)
                  .foreach(
                    candidate =>
                      validateNewPlacingInSingleBox(minimalRectangle, candidate, placement, boxLength) mustBe true
                  )
            }
            newSolution
        }
      }
    }
  }

}

private class AllCandidatesTopLeftFirstPlacingAlgorithm(
  override val boxLength: Int
) extends BinPackingTopLeftFirstPlacing {

  def placeTopLeftFirst(rectangle: Rectangle, solution: SimpleBinPackingSolution): SimpleBinPackingSolution = {
    val (placedRectangle, placing) = placeRectangleInFirstPossiblePosition(rectangle, solution.getPlacementsPerBox)
    solution.updated(placedRectangle, placing)
  }

  private def placeRectangleInFirstPossiblePosition(
    rectangle: Rectangle,
    placementsPerBox: Map[Int, Map[Rectangle, Coordinates]]
  ): (Rectangle, Placing) = {
    val maxBoxId = placementsPerBox.keys.maxOption.getOrElse(0)
    placementsPerBox.toSeq.sortBy {
      case (boxId, _) => boxId
    }.foldLeft(Option.empty[(Rectangle, Placing)]) {
        case (foundPlacing, (boxId, placement)) =>
          foundPlacing.orElse(
            placeRectangleInBoxAtMostTopLeftPoint(rectangle, placement, considerRotation = true).map {
              case (rectangle, coordinates) => rectangle -> Placing(Box(boxId, boxLength), coordinates)
            }
          )
      }
      .getOrElse(
        rectangle -> Placing(
          Box(maxBoxId + 1, boxLength),
          Coordinates(0, 0)
        )
      )
  }

}
