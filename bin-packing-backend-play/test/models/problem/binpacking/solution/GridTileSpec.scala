package models.problem.binpacking.solution

import org.scalatest.MustMatchers
import org.scalatest.WordSpec

class GridTileSpec extends WordSpec with MustMatchers {

  "GridTile" should {

    "add a rectangle correctly" in {
      val gridTile = GridTile(Set(Rectangle(1, 4, 4)))
      gridTile.added(Rectangle(2, 3, 3)) mustEqual GridTile(Set(
        Rectangle(1, 4, 4),
        Rectangle(2, 3, 3)
      ))
    }

    "remove a rectangle correctly" in {
      val gridTile = GridTile(Set(
        Rectangle(1, 4, 4),
        Rectangle(2, 3, 3)
      ))
      gridTile.removed(Rectangle(2, 3, 3)) mustEqual GridTile(Set(Rectangle(1, 4, 4)))
    }

    "throw an exception" when {
      "attempting to remove a rectangle that is not present" in {
        val gridTile = GridTile(Set(Rectangle(1, 4, 4)))
        an[IllegalArgumentException] mustBe thrownBy {
          gridTile.removed(Rectangle(2, 3, 3))
        }
      }
    }
  }

}
