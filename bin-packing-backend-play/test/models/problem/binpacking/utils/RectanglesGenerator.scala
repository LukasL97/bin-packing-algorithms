package models.problem.binpacking.utils

import models.problem.binpacking.solution.Rectangle

import scala.util.Random

trait RectanglesGenerator {

  def withRectangles[A](amount: Int, widthRange: (Int, Int), heightRange: (Int, Int))(f: Seq[Rectangle] => A): A = {
    f(generate(amount, widthRange, heightRange))
  }

  private def generate(amount: Int, widthRange: (Int, Int), heightRange: (Int, Int)): Seq[Rectangle] = {
    val (widthMin, widthMax) = widthRange
    val (heightMin, heightMax) = heightRange
    (1 to amount)
      .map(
        index =>
          Rectangle(
            index,
            widthMin + Random.nextInt(widthMax - widthMin + 1),
            heightMin + Random.nextInt(heightMax - heightMin + 1)
        )
      )
  }

}
