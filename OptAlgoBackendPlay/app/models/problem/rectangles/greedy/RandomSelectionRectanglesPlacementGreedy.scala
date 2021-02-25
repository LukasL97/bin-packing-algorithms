package models.problem.rectangles.greedy
import models.problem.rectangles.Rectangle

class RandomSelectionRectanglesPlacementGreedy(
  override val boxLength: Int,
  override val numRectangles: Int,
  override val rectangleWidthRange: (Int, Int),
  override val rectangleHeightRange: (Int, Int)
) extends RectanglesPlacementGreedy {

  override val selectionHandler: RectanglesPlacementSelectionHandler =
    new RandomSelectionRectanglesPlacementSelectionHandler(
      boxLength,
      rectangles
    )
}

class RandomSelectionRectanglesPlacementSelectionHandler(
  override val boxLength: Int,
  override val candidates: Iterable[Rectangle]
) extends RectanglesPlacementSelectionHandler {

  override def selectNextCandidate(candidates: Iterable[Rectangle]): (Rectangle, Iterable[Rectangle]) = {
    (candidates.head, candidates.tail)
  }
}
