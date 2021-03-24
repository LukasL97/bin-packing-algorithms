package models.problem.binpacking.greedy.candidatesupported

import models.problem.binpacking.solution.Rectangle

class SizeOrderedBinPackingGreedy(
  override val boxLength: Int,
  override val numRectangles: Int,
  override val rectangleWidthRange: (Int, Int),
  override val rectangleHeightRange: (Int, Int)
) extends CandidateSupportedBinPackingGreedy {

  override val selectionHandler: CandidateSupportedBinPackingSelectionHandler = new SizeOrderedBinPackingSelectionHandler(
    boxLength,
    rectangles
  )
}

/**
 * Select candidates ordered by their size (area) in descending order
 */
class SizeOrderedBinPackingSelectionHandler(
  override val boxLength: Int,
  override val candidates: Iterable[Rectangle]
) extends CandidateSupportedBinPackingSelectionHandler {

  private implicit val sizeOrdering: Ordering[Rectangle] = new Ordering[Rectangle] {
    private def size(rectangle: Rectangle): Int = rectangle.width * rectangle.height

    override def compare(x: Rectangle, y: Rectangle): Int = size(x) - size(y)
  }

  override def selectNextCandidate(candidates: Iterable[Rectangle]): (Rectangle, Iterable[Rectangle]) = {
    val sizeOrderedCandidates = candidates.toSeq.sorted.reverse
    (sizeOrderedCandidates.head, sizeOrderedCandidates.tail)
  }

}
