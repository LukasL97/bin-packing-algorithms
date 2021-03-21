package models.problem.binpacking.localsearch.evaluation

import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.SimpleBinPackingSolution
import org.scalatest.MustMatchers
import org.scalatest.WordSpec

class BoxWeightedTopLeftFirstEvaluationSpec extends WordSpec with MustMatchers {

  private val boxLength_ = 10
  private val evaluator = new BoxWeightedTopLeftFirstEvaluation {
    override val boxLength: Int = boxLength_
  }

  private val epsilon = 10e-4

  "BoxWeightedTopLeftFirstEvaluation" should {

    "build linear point cost function whose point costs sum up to 1 for all points in a box" when {
      "given some box length" in {
        val minimalCostWeight = 0.9
        val linearPointCostFunction = evaluator.buildLinearPointCostFunction(minimalCostWeight, boxLength_)
        val boxPoints = (0 until boxLength_).flatMap(x => (0 until boxLength_).map(y => (x, y)))
        val boxPointCosts = boxPoints.map { case (x, y) => linearPointCostFunction(x + y) }
        boxPointCosts.foreach { cost =>
          cost must be > 0.0
          cost must be < 1.0
        }
        boxPointCosts.sum must equal(1.0 +- epsilon)
      }
    }

    "calculate single rectangle cost correctly" when {
      val pointCostFunction = evaluator.buildLinearPointCostFunction(0.9, boxLength_)

      "given a rectangle filling the entire box" in {
        evaluator.calculateRectangleCost(pointCostFunction, Rectangle(0, boxLength_, boxLength_), Coordinates(0, 0)) must equal(1.0 +- epsilon)
      }

      "given a rectangle with size 0" in {
        evaluator.calculateRectangleCost(pointCostFunction, Rectangle(0, 0, 0), Coordinates(0, 0)) must equal(0.0 +- epsilon)
      }

      "given rectangles in all 4 corners of the box" in {
        val topLeftCost = evaluator.calculateRectangleCost(
          pointCostFunction,
          Rectangle(0, boxLength_ / 2, boxLength_ / 2),
          Coordinates(0, 0)
        )
        val topRightCost = evaluator.calculateRectangleCost(
          pointCostFunction,
          Rectangle(1, boxLength_ / 2, boxLength_ / 2),
          Coordinates(boxLength_ / 2, 0)
        )
        val bottomLeftCost = evaluator.calculateRectangleCost(
          pointCostFunction,
          Rectangle(2, boxLength_ / 2, boxLength_ / 2),
          Coordinates(boxLength_ / 2, 0)
        )
        val bottomRightCost = evaluator.calculateRectangleCost(
          pointCostFunction,
          Rectangle(3, boxLength_ / 2, boxLength_ / 2),
          Coordinates(boxLength_ / 2, boxLength_ / 2)
        )
        topRightCost must equal(0.25 +- epsilon)
        bottomLeftCost must equal(0.25 +- epsilon)
        topLeftCost must be < 0.25
        bottomRightCost must be > 0.25
        topLeftCost + bottomRightCost mustEqual (0.5 +- epsilon)
      }

      "given a non-square rectangle" in {
        val rectangle = Rectangle(1, 3, 4)
        val coordinates = Coordinates(2, 4)
        val rectanglePoints = Seq(
          Coordinates(2, 4),
          Coordinates(2, 5),
          Coordinates(2, 6),
          Coordinates(2, 7),
          Coordinates(3, 4),
          Coordinates(3, 5),
          Coordinates(3, 6),
          Coordinates(3, 7),
          Coordinates(4, 4),
          Coordinates(4, 5),
          Coordinates(4, 6),
          Coordinates(4, 7),
        )
        evaluator.calculateRectangleCost(
          pointCostFunction,
          rectangle,
          coordinates
        ) must equal(rectanglePoints.map(c => pointCostFunction(c.x + c.y)).sum +- epsilon)
      }
    }

    "evaluate a minimal change in box 2 with maximal change in box 1 as improvement" when {
      "comparing two such solutions" in {
        val box1 = Box(1, boxLength_)
        val box2 = Box(2, boxLength_)
        val originalSolution = SimpleBinPackingSolution(Map(
          Rectangle(1, 1, 1) -> Placing(box1, Coordinates(0, 0)),
          Rectangle(2, 1, 1) -> Placing(box2, Coordinates(4, 3))
        ))
        val improvedSolution = SimpleBinPackingSolution(Map(
          Rectangle(1, boxLength_, boxLength_) -> Placing(box1, Coordinates(0, 0)),
          Rectangle(2, 1, 1) -> Placing(box2, Coordinates(3, 3))
        ))
        evaluator.evaluate(improvedSolution) must be < evaluator.evaluate(originalSolution)
      }
    }

  }

}
