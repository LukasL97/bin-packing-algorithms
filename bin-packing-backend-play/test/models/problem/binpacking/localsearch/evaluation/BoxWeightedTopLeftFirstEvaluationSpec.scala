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

  "BoxWeightedTopLeftFirstEvaluation" should {

    "build linear point cost function whose point costs sum up to 1 for all points in a box" when {
      "given some box length" in {
        val minimalCostWeight = 0.9
        val linearPointCostFunction = evaluator.buildLinearPointCostFunction(minimalCostWeight, boxLength_)
        val boxPoints = (0 until boxLength_).flatMap(x => (0 until boxLength_).map(y => (x, y)))
        val boxPointCosts = boxPoints.map { case (x, y) => linearPointCostFunction(Coordinates(x, y)) }
        boxPointCosts.foreach { cost =>
          cost must be > BigDecimal(0.0)
          cost must be < BigDecimal(1.0)
        }
        boxPointCosts.sum mustEqual BigDecimal(1.0)
      }
    }

    "calculate single rectangle cost correctly" when {
      val pointCostFunction = evaluator.buildLinearPointCostFunction(0.9, boxLength_)

      "given a rectangle filling the entire box" in {
        evaluator.calculateRectangleCost(pointCostFunction, Rectangle(0, boxLength_, boxLength_), Coordinates(0, 0)) mustEqual 1.0
      }

      "given a rectangle with size 0" in {
        evaluator.calculateRectangleCost(pointCostFunction, Rectangle(0, 0, 0), Coordinates(0, 0)) mustEqual 0.0
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
        val epsilon = 10e-6
        topRightCost must equal(BigDecimal(0.25) +- epsilon)
        bottomLeftCost must equal(BigDecimal(0.25) +- epsilon)
        topLeftCost must be < BigDecimal(0.25)
        bottomRightCost must be > BigDecimal(0.25)
        topLeftCost + bottomRightCost mustEqual (BigDecimal(0.5) +- epsilon)
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
