package utils

import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.SimpleBinPackingSolution
import models.problem.binpacking.solution.update.RectanglesChanged
import org.scalatest.MustMatchers
import org.scalatest.WordSpec
import BinPackingSolutionSerializationUtil.formats

class BinPackingSolutionSerializationUtilSpec extends WordSpec with MustMatchers {

  "BinPackingSolutionSerializationUtil" should {
    "serialize and deserialize a SimpleBinPackingSolution" in {
      val solution = new SimpleBinPackingSolution(
        Map(Rectangle(1, 2, 3) -> Placing(Box(1, 10), Coordinates(1, 2))),
        RectanglesChanged(Set(1))
      )
      val json = SerializationUtil.toJson(solution)
      SerializationUtil.fromJson[SimpleBinPackingSolution](json) mustEqual solution
    }
  }

}
