package models.problem.binpacking.solution

import org.scalatest.MustMatchers
import org.scalatest.WordSpec

class GridSpec extends WordSpec with MustMatchers {

  private val tileLength = 5

  "Grid" should {

    "initialize with empty tiles correctly" when {

      "given boxLength as a multiple of tileLength" in {
        val grid = Grid.apply(
          boxLength = tileLength * 2,
          tileLength = tileLength
        )
        grid mustEqual Grid(
          Map(
            GridIndex(0, 0) -> GridTile(Set.empty),
            GridIndex(1, 0) -> GridTile(Set.empty),
            GridIndex(0, 1) -> GridTile(Set.empty),
            GridIndex(1, 1) -> GridTile(Set.empty)
          ),
          tileLength
        )
      }

      "given a boxLength that is not a multiple of tileLength" in {
        val grid = Grid.apply(
          boxLength = tileLength * 2 - 1,
          tileLength = tileLength
        )
        grid mustEqual Grid(
          Map(
            GridIndex(0, 0) -> GridTile(Set.empty),
            GridIndex(1, 0) -> GridTile(Set.empty),
            GridIndex(0, 1) -> GridTile(Set.empty),
            GridIndex(1, 1) -> GridTile(Set.empty)
          ),
          tileLength
        )
      }
    }

    "add rectangle correctly" in {
      val grid = Grid(
        Map(
          GridIndex(0, 0) -> GridTile(Set(Rectangle(1, 1, 1))),
          GridIndex(1, 0) -> GridTile(Set.empty),
          GridIndex(0, 1) -> GridTile(Set.empty),
          GridIndex(1, 1) -> GridTile(Set.empty),
        ),
        tileLength
      )
      val rectangle = Rectangle(2, tileLength * 2, 3)
      val coordinates = Coordinates(0, tileLength)
      grid.added(rectangle, coordinates) mustEqual Grid(
        Map(
          GridIndex(0, 0) -> GridTile(Set(Rectangle(1, 1, 1))),
          GridIndex(1, 0) -> GridTile(Set.empty),
          GridIndex(0, 1) -> GridTile(Set(rectangle)),
          GridIndex(1, 1) -> GridTile(Set(rectangle)),
        ),
        tileLength
      )
    }

    "remove rectangle correctly" in {
      val rectangle = Rectangle(2, tileLength * 2, 3)
      val coordinates = Coordinates(0, tileLength)
      val otherRectangle = Rectangle(1, 1, 1)
      val grid = Grid(
        Map(
          GridIndex(0, 0) -> GridTile(Set.empty),
          GridIndex(1, 0) -> GridTile(Set.empty),
          GridIndex(0, 1) -> GridTile(Set(rectangle, otherRectangle)),
          GridIndex(1, 1) -> GridTile(Set(rectangle)),
        ),
        tileLength
      )
      grid.removed(rectangle, coordinates) mustEqual Grid(
        Map(
          GridIndex(0, 0) -> GridTile(Set.empty),
          GridIndex(1, 0) -> GridTile(Set.empty),
          GridIndex(0, 1) -> GridTile(Set(otherRectangle)),
          GridIndex(1, 1) -> GridTile(Set.empty),
        ),
        tileLength
      )
    }

  }

}
