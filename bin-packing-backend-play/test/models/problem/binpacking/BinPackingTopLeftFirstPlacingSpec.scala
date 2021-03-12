package models.problem.binpacking

import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Rectangle
import org.scalatest.MustMatchers
import org.scalatest.WordSpec

class BinPackingTopLeftFirstPlacingSpec extends WordSpec with MustMatchers {

  private val boxLength_ = 5

  private val placer = new BinPackingTopLeftFirstPlacing {
    override val boxLength: Int = boxLength_
  }

  "BinPackingTopLeftFirstPlacing" should {
    "consider the rotated version of a rectangle correctly" when {
      "given an existing placement in which only the rotated version fits" in {
        val placement = Map(Rectangle(1, boxLength_, 4) -> Coordinates(0, 0))
        val rectangle = Rectangle(2, 1, boxLength_)
        placer.placeRectangleInBoxAtMostTopLeftPoint(rectangle, placement, considerRotation = true) mustEqual {
          Some(rectangle.rotated -> Coordinates(0, 4))
        }
      }
    }

    "return None" when {
      "given a rectangle that does not fit into a placement" in {
        val placement = Map(Rectangle(1, boxLength_, 4) -> Coordinates(0, 0))
        val rectangle = Rectangle(2, 2, 2)
        placer.placeRectangleInBoxAtMostTopLeftPoint(rectangle, placement, considerRotation = true) mustBe None
      }
    }
  }

}
