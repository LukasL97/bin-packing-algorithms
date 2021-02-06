package actors

import dao.RectanglesPlacementSolutionStepDAO
import models.problem.rectangles.RectanglesPlacement
import models.problem.rectangles.RectanglesPlacementLocalSearch
import models.problem.rectangles.RectanglesPlacementSolution
import models.problem.rectangles.RectanglesPlacementSolutionHandler
import org.mongodb.scala.Completed
import org.scalamock.scalatest.MockFactory
import org.scalatest.WordSpec

import scala.concurrent.Future

class RectanglesPlacementExecutorSpec extends WordSpec with MockFactory {

  private val dao = mock[RectanglesPlacementSolutionStepDAO]

  private val executor = new RectanglesPlacementExecutor(dao)

  "RectanglesPlacementExecutor" should {
    "dump intermediate solution steps correctly" when {
      "receiving rectanglesPlacement with top left start solution, bottom right optimum, and diagonal neighborhood path" in {
        val runId = "runId"

        val rectanglesPlacement = new RectanglesPlacement {
          override val boxLength: Int = 3
          override val numRectangles: Int = 1
          override val rectangleWidthRange: (Int, Int) = (1, 1)
          override val rectangleHeightRange: (Int, Int) = (1, 1)

          override val solutionHandler: RectanglesPlacementSolutionHandler = new RectanglesPlacementSolutionHandler {
            override def createArbitraryFeasibleSolution(): RectanglesPlacementSolution = RectanglesPlacementSolution(
              Map(rectangles.head -> (boxes.head, (0, 0)))
            )

            override def getNeighborhood(solution: RectanglesPlacementSolution): Set[RectanglesPlacementSolution] = Set(
              Option(RectanglesPlacementSolution(
                solution.placement.map {
                  case (rectangle, (box, (x, y))) => rectangle -> (box, (x + 1, y + 1))
                }
              )).filter(solutionHandler.isFeasible)
            ).flatten

            override def evaluate(solution: RectanglesPlacementSolution): Double = solution.placement.head match {
              case (rectangle, (box, (x, y))) => -(x + y)
            }
          }

          private val thisSolutionHandler = solutionHandler
          override val localSearch: RectanglesPlacementLocalSearch = new RectanglesPlacementLocalSearch {
            override val solutionHandler: RectanglesPlacementSolutionHandler = thisSolutionHandler
          }
        }

        (dao.dumpSolutionStep _)
          .expects(RectanglesPlacementSolutionStep(runId, 0, solution = RectanglesPlacementSolution(
            Map(rectanglesPlacement.rectangles.head -> (rectanglesPlacement.boxes.head, (0, 0)))
          )))
          .returns(null)
        (dao.dumpSolutionStep _)
          .expects(RectanglesPlacementSolutionStep(runId, 1, solution = RectanglesPlacementSolution(
            Map(rectanglesPlacement.rectangles.head -> (rectanglesPlacement.boxes.head, (1, 1)))
          )))
          .returns(null)
        (dao.dumpSolutionStep _)
          .expects(RectanglesPlacementSolutionStep(runId, 2, solution = RectanglesPlacementSolution(
            Map(rectanglesPlacement.rectangles.head -> (rectanglesPlacement.boxes.head, (2, 2)))
          )))
          .returns(null)
        (dao.dumpSolutionStep _)
          .expects(RectanglesPlacementSolutionStep(runId, 3, solution = RectanglesPlacementSolution(
            Map(rectanglesPlacement.rectangles.head -> (rectanglesPlacement.boxes.head, (2, 2)))
          )))
          .returns(null)

        executor.execute(runId, rectanglesPlacement)

      }
    }
  }

}
