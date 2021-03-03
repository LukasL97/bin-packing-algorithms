package models.problem.binpacking

import scala.util.Random

trait BinPacking {

  val boxLength: Int
  val numRectangles: Int
  val rectangleWidthRange: (Int, Int)
  val rectangleHeightRange: (Int, Int)

  private lazy val (rectangleWidthMin, rectangleWidthMax) = rectangleWidthRange
  private lazy val (rectangleHeightMin, rectangleHeightMax) = rectangleHeightRange

  lazy val rectangles: Set[Rectangle] = (1 to numRectangles)
    .map(
      index =>
        Rectangle(
          index,
          rectangleWidthMin + Random.nextInt(rectangleWidthMax - rectangleWidthMin + 1),
          rectangleHeightMin + Random.nextInt(rectangleHeightMax - rectangleHeightMin + 1)
        )
    )
    .toSet

  def startSolution: BinPackingSolution

}
