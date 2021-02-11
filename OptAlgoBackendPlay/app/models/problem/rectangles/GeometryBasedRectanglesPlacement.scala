package models.problem.rectangles

import play.api.Logging

class GeometryBasedRectanglesPlacement(
  override val boxLength: Int,
  override val numRectangles: Int,
  override val rectangleWidthRange: (Int, Int),
  override val rectangleHeightRange: (Int, Int)
) extends RectanglesPlacement {

  override val solutionHandler: RectanglesPlacementSolutionHandler =
    new GeometryBasedRectanglesPlacementSolutionHandler(boxes, rectangles)

  private val thisSolutionHandler = solutionHandler

  override val localSearch: RectanglesPlacementLocalSearch = new RectanglesPlacementLocalSearch {
    override val solutionHandler: RectanglesPlacementSolutionHandler = thisSolutionHandler
  }

}

class GeometryBasedRectanglesPlacementSolutionHandler(
  boxes: Set[Box],
  rectangles: Set[Rectangle]
) extends RectanglesPlacementSolutionHandler with Logging {

  require(Set(boxes.map(_.width)).size == 1, "Not all boxes have the same width")
  require(Set(boxes.map(_.height)).size == 1, "Not all boxes have the same height")
  require(boxes.head.width == boxes.head.height, "Boxes have differing width and height")

  private val boxLength = boxes.head.width

  override def createArbitraryFeasibleSolution(): RectanglesPlacementSolution = {
    require(boxes.size == rectangles.size)
    val solution = RectanglesPlacementSolution(
      (rectangles zip boxes).map {
        case (rectangle, box) => rectangle -> (box, (0, 0))
      }.toMap
    )
    if (isFeasible(solution)) {
      solution
    } else {
      throw new RuntimeException("Created infeasible solution as starting solution")
    }
  }

  override def getNeighborhood(solution: RectanglesPlacementSolution): Set[RectanglesPlacementSolution] = {
    val solutionsWithBoxPullUp = solution.placement.collect {
      case (rectangle, (Box(id, width, height), (x, y))) if id > 1 =>
        RectanglesPlacementSolution(
          solution.placement.updated(
            rectangle,
            (Box(id - 1, width, height), (width - rectangle.width, height - rectangle.height))
          )
        )
    }.toSet
    val solutionsWithUpShift = solution.placement.map {
      case (rectangle, (box, (x, y))) =>
        RectanglesPlacementSolution(
          solution.placement.updated(
            rectangle,
            (box, (x, y - 1))
          )
        )
    }.toSet
    val solutionsWithLeftShift = solution.placement.map {
      case (rectangle, (box, (x, y))) =>
        RectanglesPlacementSolution(
          solution.placement.updated(
            rectangle,
            (box, (x - 1, y))
          )
        )
    }.toSet
    val neighborhood = (solutionsWithBoxPullUp ++ solutionsWithUpShift ++ solutionsWithLeftShift).filter(isFeasible)
    logger.trace(s"Get neighborhood of size ${neighborhood.size}")
    neighborhood
  }

  def buildLinearPointCostFunction(minimalCostWeight: BigDecimal, boxLength: Int): (Int, Int) => BigDecimal = {
    require(minimalCostWeight < 1.0 && minimalCostWeight > 0.0)
    val slope = 2 * (1 - minimalCostWeight) / (2 * boxLength - 2)
    (x: Int, y: Int) => (slope * (x + y) + minimalCostWeight) / (boxLength * boxLength)
  }

  def calculateRectangleCost(
    pointCostFunction: (Int, Int) => BigDecimal,
    rectangle: Rectangle,
    coordinates: (Int, Int)
  ): BigDecimal = {
    (coordinates._1 until (coordinates._1 + rectangle.width))
      .flatMap(x => (coordinates._2 until (coordinates._2 + rectangle.height)).map(y => (x, y)))
      .map { case (x, y) => pointCostFunction(x, y) }
      .sum
  }

  def calculateBoxCostFactor(pointCostFunction: (Int, Int) => BigDecimal, id: Int): BigDecimal = {
     ((1 / pointCostFunction(0, 0)) + 1).pow(id)
  }

  override def evaluate(solution: RectanglesPlacementSolution): BigDecimal = {
    val minimalCostWeight = 0.9
    val pointCostFunction = buildLinearPointCostFunction(minimalCostWeight, boxLength)
    val boxIds = boxes.toSeq.map(_.id).sorted
    val boxSkewedFillRates = boxIds.map { id =>
      val rectanglesInBox = solution.placement.collect {
        case (rectangle, (box, coordinates)) if box.id == id => rectangle -> coordinates
      }
      rectanglesInBox.map {
        case (rectangle, coordinates) => calculateRectangleCost(pointCostFunction, rectangle, coordinates)
      }.sum
    }
    val boxCostFactors = boxIds.map(id => calculateBoxCostFactor(pointCostFunction, id))
    (boxCostFactors zip boxSkewedFillRates)
      .map {
        case (factor, rate) => factor * rate
      }.sum
  }

}
