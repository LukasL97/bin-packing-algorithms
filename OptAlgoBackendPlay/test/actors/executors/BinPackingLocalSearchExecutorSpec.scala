package actors.executors

import actors.BinPackingSolutionStep
import dao.BinPackingSolutionStepDAO
import models.problem.binpacking.Box
import models.problem.binpacking.Coordinates
import models.problem.binpacking.Placing
import models.problem.binpacking.BinPackingSolution
import models.problem.binpacking.localsearch.BinPackingLocalSearch
import models.problem.binpacking.localsearch.BinPackingSolutionHandler
import org.scalamock.scalatest.MockFactory
import org.scalatest.WordSpec

class BinPackingLocalSearchExecutorSpec extends WordSpec with MockFactory {

  private val dao = mock[BinPackingSolutionStepDAO]

  private val executor = new BinPackingLocalSearchExecutor(dao)

  "BinPackingLocalSearchExecutor" should {
    "dump intermediate solution steps correctly" when {
      "receiving binPacking with top left start solution, bottom right optimum, and diagonal neighborhood path" in {
        val runId = "runId"

        val boxLength_ = 3
        val box = Box(1, boxLength_, boxLength_)

        val binPacking: BinPackingLocalSearch = new BinPackingLocalSearch {
          override val boxLength: Int = boxLength_
          override val numRectangles: Int = 1
          override val rectangleWidthRange: (Int, Int) = (1, 1)
          override val rectangleHeightRange: (Int, Int) = (1, 1)

          override val solutionHandler: BinPackingSolutionHandler = new BinPackingSolutionHandler {
            override def createArbitraryFeasibleSolution(): BinPackingSolution = BinPackingSolution(
              Map(rectangles.head -> Placing(box, Coordinates(0, 0)))
            )

            override def getNeighborhood(solution: BinPackingSolution): Set[BinPackingSolution] = Set(
              Option(BinPackingSolution(
                solution.placement.map {
                  case (rectangle, Placing(box, Coordinates(x, y))) => rectangle -> Placing(box, Coordinates(x + 1, y + 1))
                }
              )).filter(solutionHandler.isFeasible)
            ).flatten

            override def evaluate(solution: BinPackingSolution): BigDecimal = solution.placement.head match {
              case (rectangle, Placing(box, Coordinates(x, y))) => -(x + y)
            }
          }
        }

        (dao.dumpSolutionStep _)
          .expects(BinPackingSolutionStep(runId, 0, solution = BinPackingSolution(
            Map(binPacking.rectangles.head -> Placing(box, Coordinates(0, 0)))
          )))
          .returns(null)
        (dao.dumpSolutionStep _)
          .expects(BinPackingSolutionStep(runId, 1, solution = BinPackingSolution(
            Map(binPacking.rectangles.head -> Placing(box, Coordinates(1, 1)))
          )))
          .returns(null)
        (dao.dumpSolutionStep _)
          .expects(BinPackingSolutionStep(runId, 2, solution = BinPackingSolution(
            Map(binPacking.rectangles.head -> Placing(box, Coordinates(2, 2)))
          )))
          .returns(null)
        (dao.dumpSolutionStep _)
          .expects(BinPackingSolutionStep(runId, 3, solution = BinPackingSolution(
            Map(binPacking.rectangles.head -> Placing(box, Coordinates(2, 2)))
          ), finished = true))
          .returns(null)

        executor.execute(runId, binPacking)

      }
    }
  }

}
