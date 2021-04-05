package utils

import models.problem.binpacking
import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.RectanglePermutationBinPackingSolutionRepresentation
import models.problem.binpacking.solution.SimpleBinPackingSolutionRepresentation
import models.problem.binpacking.solution.update.RectanglesChanged
import org.scalatest.MustMatchers
import org.scalatest.WordSpec
import utils.BinPackingSolutionRepresentationSerializationUtil.formats

class BinPackingSolutionRepresentationSerializationUtilSpec extends WordSpec with MustMatchers {

  "BinPackingSolutionRepresentationSerializationUtil" should {
    "serialize and deserialize a SimpleBinPackingSolutionRepresentation" in {
      val solution = SimpleBinPackingSolutionRepresentation(
        Map(Rectangle(1, 2, 3) -> Placing(Box(1, 10), Coordinates(1, 2))),
        RectanglesChanged(Set(1))
      )
      val json = SerializationUtil.toJson(solution)
      SerializationUtil.fromJson[SimpleBinPackingSolutionRepresentation](json) mustEqual solution
    }
    "serialize and deserialize a RectanglePermutationBinPackingSolutionRepresentation" in {
      val solution = RectanglePermutationBinPackingSolutionRepresentation(
        Map(
          Rectangle(1, 1, 1) -> Placing(Box(1, 10), Coordinates(0, 0)),
          Rectangle(2, 2, 4) -> Placing(Box(1, 10), Coordinates(4, 6)),
          Rectangle(3, 2, 3) -> Placing(Box(2, 10), Coordinates(1, 2))
        ),
        RectanglesChanged(Set(2, 3)),
        Seq(1, 3, 2)
      )
      val json = SerializationUtil.toJson(solution)
      SerializationUtil.fromJson[RectanglePermutationBinPackingSolutionRepresentation](json) mustEqual solution
    }
  }

}
