package models.problem.rectangles

import org.scalatest.MustMatchers
import org.scalatest.WordSpec

class GeometryBasedRectanglesPlacementSolutionHandlerSpec extends WordSpec with MustMatchers {

  "GeometryBasedRectanglesPlacementSolutionHandler" should {

    "build linear point cost function whose point costs sum up to 1 for all points in a box" when {
      "given some box length" in {
        val handler = new GeometryBasedRectanglesPlacementSolutionHandler(Set(), Set())
        val boxLength = 10
        val minimalCostWeight = 0.9
        val linearPointCostFunction = handler.buildLinearPointCostFunction(minimalCostWeight, boxLength)
        val boxPoints = (0 until boxLength).flatMap(x => (0 until boxLength).map(y => (x, y)))
        val boxPointCosts = boxPoints.map { case (x, y) => linearPointCostFunction(x, y) }
        boxPointCosts.foreach { cost =>
          cost must be > BigDecimal(0.0)
          cost must be < BigDecimal(1.0)
        }
        boxPointCosts.sum mustEqual BigDecimal(1.0)
      }
    }

    "calculate single rectangle cost correctly" when {
      val handler = new GeometryBasedRectanglesPlacementSolutionHandler(Set(), Set())
      val boxLength = 10
      val pointCostFunction = handler.buildLinearPointCostFunction(0.9, boxLength)

      "given a rectangle filling the entire box" in {
        handler.calculateRectangleCost(pointCostFunction, Rectangle(0, boxLength, boxLength), (0, 0)) mustEqual 1.0
      }

      "given a rectangle with size 0" in {
        handler.calculateRectangleCost(pointCostFunction, Rectangle(0, 0, 0), (0, 0)) mustEqual 0.0
      }

      "given rectangles in all 4 corners of the box" in {
        val topLeftCost = handler.calculateRectangleCost(
          pointCostFunction,
          Rectangle(0, boxLength / 2, boxLength / 2),
          (0, 0)
        )
        val topRightCost = handler.calculateRectangleCost(
          pointCostFunction,
          Rectangle(1, boxLength / 2, boxLength / 2),
          (boxLength / 2, 0)
        )
        val bottomLeftCost = handler.calculateRectangleCost(
          pointCostFunction,
          Rectangle(2, boxLength / 2, boxLength / 2),
          (boxLength / 2, 0)
        )
        val bottomRightCost = handler.calculateRectangleCost(
          pointCostFunction,
          Rectangle(3, boxLength / 2, boxLength / 2),
          (boxLength / 2, boxLength / 2)
        )
        val epsilon = 10e-6
        topRightCost must equal(BigDecimal(0.25) +- epsilon)
        bottomLeftCost must equal(BigDecimal(0.25) +- epsilon)
        topLeftCost must be < BigDecimal(0.25)
        bottomRightCost must be > BigDecimal(0.25)
        topLeftCost + bottomRightCost mustEqual (BigDecimal(0.5) +- epsilon)
      }
    }
  }

}
