package models.problem.rectangles.localsearch

import models.algorithm.LocalSearch
import models.algorithm.SolutionHandler
import models.problem.rectangles.Coordinates
import models.problem.rectangles.Placing
import models.problem.rectangles.Rectangle
import models.problem.rectangles.RectanglesPlacement
import models.problem.rectangles.RectanglesPlacementSolution
import models.problem.rectangles.RectanglesPlacementSolutionValidator

import scala.util.Random

trait RectanglesPlacementLocalSearch extends RectanglesPlacement {
  val solutionHandler: RectanglesPlacementSolutionHandler
  lazy val localSearch = new LocalSearch[RectanglesPlacementSolution](solutionHandler)

  override def startSolution: RectanglesPlacementSolution = localSearch.startSolution
}

trait RectanglesPlacementSolutionHandler
    extends SolutionHandler[RectanglesPlacementSolution] with RectanglesPlacementSolutionValidator
