package models.problem.binpacking.solution

import models.problem.binpacking.solution.transformation.RectanglePlacingUpdateSupport
import models.problem.binpacking.solution.update.Update
import models.problem.binpacking.solution.update.UpdateStoringSupport

case class GridInfoBinPackingSolution(
  override val placement: Map[Rectangle, Placing],
  override val grids: Map[Int, Grid],
  override val boxLength: Int,
  override val tileLength: Int,
  override val update: Update
) extends BinPackingSolution with GridInfo with RectanglePlacingUpdateSupport[GridInfoBinPackingSolution]
    with UpdateStoringSupport[GridInfoBinPackingSolution] {

  override def asSimpleSolution: SimpleBinPackingSolution = new SimpleBinPackingSolution(placement, update)

  override def updated(rectangle: Rectangle, placing: Placing): GridInfoBinPackingSolution = {
    val rectanglePlacingToOverride = placement.collectFirst {
      case (oldRectangle, oldPlacing) if oldRectangle.id == rectangle.id => oldRectangle -> oldPlacing
    }
    val updatedPlacement = rectanglePlacingToOverride.map {
      case (rectangleToOverride, _) =>
        placement.filterNot {
          case (rectangle, _) => rectangle.id == rectangleToOverride.id
        }
    }.getOrElse(placement) + {
      rectangle -> placing
    }
    val updatedGrids = rectanglePlacingToOverride.map {
      case (rectangleToOverride, placingToOverride) =>
        replaceRectangleInGrids(rectangleToOverride, placingToOverride, rectangle, placing)
    }.getOrElse(placeRectangleInGrids(rectangle, placing))
    copy(
      placement = updatedPlacement,
      grids = updatedGrids,
      boxLength = boxLength,
      tileLength = tileLength
    )
  }

  override def setUpdate(update: Update): GridInfoBinPackingSolution = copy(update = update)
}

trait GridInfo {
  val grids: Map[Int, Grid]

  val boxLength: Int
  val tileLength: Int

  protected def placeRectangleInGrids(
    rectangle: Rectangle,
    placing: Placing
  ): Map[Int, Grid] = {
    updateSingleGrid(grids, placing.box.id, _.added(rectangle, placing.coordinates))
  }

  protected def replaceRectangleInGrids(
    oldRectangle: Rectangle,
    oldPlacing: Placing,
    newRectangle: Rectangle,
    newPlacing: Placing
  ): Map[Int, Grid] = {
    val gridsWithOldRectangleRemoved =
      updateSingleGrid(grids, oldPlacing.box.id, _.removed(oldRectangle, oldPlacing.coordinates))
    updateSingleGrid(gridsWithOldRectangleRemoved, newPlacing.box.id, _.added(newRectangle, newPlacing.coordinates))
  }

  private def updateSingleGrid(grids: Map[Int, Grid], boxId: Int, update: Grid => Grid): Map[Int, Grid] = {
    val gridToUpdate = grids.getOrElse(boxId, Grid(boxLength, tileLength))
    grids.updated(boxId, update(gridToUpdate))
  }
}

object Grid {
  def apply(boxLength: Int, tileLength: Int): Grid = {
    val indexes = (0 until boxLength).map(_ / tileLength).distinct
    val tiles = for (x <- indexes; y <- indexes) yield GridIndex(x, y) -> GridTile(Set.empty[Rectangle])
    new Grid(tiles.toMap, tileLength)
  }
}

case class Grid(
  tiles: Map[GridIndex, GridTile],
  tileLength: Int
) {

  def at(x: Int, y: Int): GridTile = tiles(GridIndex(x, y))

  def added(rectangle: Rectangle, coordinates: Coordinates): Grid = {
    val coveredTiles = getTilesCoveredByRectangle(rectangle, coordinates)
    coveredTiles.foldLeft(this) {
      case (grid, (index, tile)) => grid.updateTile(index, tile.added(rectangle))
    }
  }

  def removed(rectangle: Rectangle, coordinates: Coordinates): Grid = {
    val coveredTiles = getTilesCoveredByRectangle(rectangle, coordinates)
    coveredTiles.foldLeft(this) {
      case (grid, (index, tile)) => grid.updateTile(index, tile.removed(rectangle))
    }
  }

  private def getTilesCoveredByRectangle(
    rectangle: Rectangle,
    coordinates: Coordinates
  ): Iterable[(GridIndex, GridTile)] = {
    val xMin = coordinates.x / tileLength
    val yMin = coordinates.y / tileLength
    val xMax = (coordinates.x + rectangle.width - 1) / tileLength
    val yMax = (coordinates.y + rectangle.height - 1) / tileLength
    for (y <- yMin to yMax; x <- xMin to xMax) yield {
      GridIndex(x, y) -> at(x, y)
    }
  }

  private def updateTile(index: GridIndex, tile: GridTile): Grid = Grid(
    tiles.updated(index, tile),
    tileLength
  )
}

case class GridIndex(x: Int, y: Int)

case class GridTile(
  rectangles: Set[Rectangle]
) {

  def removed(rectangle: Rectangle): GridTile = {
    require(rectangles.contains(rectangle))
    GridTile(
      rectangles.filterNot(_ == rectangle)
    )
  }

  def added(rectangle: Rectangle): GridTile = GridTile(
    rectangles + rectangle
  )
}
