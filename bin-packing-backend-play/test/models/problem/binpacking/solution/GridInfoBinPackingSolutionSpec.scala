package models.problem.binpacking.solution

import org.scalatest.MustMatchers
import org.scalatest.WordSpec

class GridInfoBinPackingSolutionSpec extends WordSpec with MustMatchers {

  private val boxLength = 10
  private val tileLength = 5

  "GridInfoBinPackingSolution" should {
    "update both placement and grids correctly" when  {

      "inserting a new rectangle" in {
        val solution = GridInfoBinPackingSolution(
          Map.empty,
          Map.empty,
          boxLength,
          tileLength
        )
        val rectangle = Rectangle(1, boxLength, 3)
        val boxId = 1
        val placing = Placing(Box(boxId, boxLength), Coordinates(0, tileLength + 1))
        solution.updated(rectangle, placing) mustEqual GridInfoBinPackingSolution(
          Map(rectangle -> placing),
          Map(
            boxId -> Grid(
              Map(
                GridIndex(0, 0) -> GridTile(Set.empty),
                GridIndex(1, 0) -> GridTile(Set.empty),
                GridIndex(0, 1) -> GridTile(Set(rectangle)),
                GridIndex(1, 1) -> GridTile(Set(rectangle)),
              ),
              tileLength
            )
          ),
          boxLength,
          tileLength
        )
      }

      "overriding an existing rectangle" in {
        val rectangle = Rectangle(1, boxLength, 3)
        val boxId = 1
        val oldPlacing = Placing(Box(boxId, boxLength), Coordinates(0, tileLength + 1))
        val solution = GridInfoBinPackingSolution(
          Map(rectangle -> oldPlacing),
          Map(
            boxId -> Grid(
              Map(
                GridIndex(0, 0) -> GridTile(Set.empty),
                GridIndex(1, 0) -> GridTile(Set.empty),
                GridIndex(0, 1) -> GridTile(Set(rectangle)),
                GridIndex(1, 1) -> GridTile(Set(rectangle))
              ),
              tileLength
            )
          ),
          boxLength,
          tileLength
        )
        val newPlacing = oldPlacing.copy(coordinates = Coordinates(0, 0))
        solution.updated(rectangle, newPlacing) mustEqual GridInfoBinPackingSolution(
          Map(rectangle -> newPlacing),
          Map(
            boxId -> Grid(
              Map(
                GridIndex(0, 0) -> GridTile(Set(rectangle)),
                GridIndex(1, 0) -> GridTile(Set(rectangle)),
                GridIndex(0, 1) -> GridTile(Set.empty),
                GridIndex(1, 1) -> GridTile(Set.empty)
              ),
              tileLength
            )
          ),
          boxLength,
          tileLength
        )
      }
    }
  }

}
