package models.problem.binpacking

import models.problem.binpacking.solution.Rectangle
import org.joda.time.DateTime

import java.util.Date
import java.util.UUID
import scala.util.Random

case class BinPackingInstance(
  id: String,
  creationDate: Date,
  boxLength: Int,
  numRectangles: Int,
  minWidth: Int,
  maxWidth: Int,
  minHeight: Int,
  maxHeight: Int,
  rectangles: Seq[Rectangle]
) {

  def toTupleString: String = {
    s"($boxLength, $numRectangles, $minWidth, $maxHeight, $minHeight, $maxHeight)"
  }
}

object BinPackingInstance {

  def apply(
    boxLength: Int,
    numRectangles: Int,
    minWidth: Int,
    maxWidth: Int,
    minHeight: Int,
    maxHeight: Int
  ): BinPackingInstance = {
    val id = UUID.randomUUID().toString
    val creationDate = DateTime.now().toDate
    val rectangles = generateRectangles(numRectangles, minWidth, maxWidth, minHeight, maxHeight)
    new BinPackingInstance(
      id,
      creationDate,
      boxLength,
      numRectangles,
      minWidth,
      maxWidth,
      minHeight,
      maxHeight,
      rectangles
    )
  }

  private def generateRectangles(
    numRectangles: Int,
    minWidth: Int,
    maxWidth: Int,
    minHeight: Int,
    maxHeight: Int
  ): Seq[Rectangle] = {
    (1 to numRectangles)
      .map(
        index =>
          Rectangle(
            index,
            minWidth + Random.nextInt(maxWidth - minWidth + 1),
            minHeight + Random.nextInt(maxHeight - minHeight + 1)
        )
      )
  }
}
