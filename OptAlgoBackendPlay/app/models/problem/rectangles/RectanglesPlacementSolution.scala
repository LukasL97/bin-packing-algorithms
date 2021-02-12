package models.problem.rectangles

import models.algorithm.Solution

case class RectanglesPlacementSolution(
  placement: Map[Rectangle, Placing]
) extends Solution


case class Rectangle(
  id: Int,
  width: Int,
  height: Int
)

case class Placing(
  box: Box,
  coordinates: Coordinates
)

case class Box(
  id: Int,
  width: Int,
  height: Int
)

case class Coordinates(
  x: Int,
  y: Int
)


