package actors.executors

import actors.BinPackingSolutionStep
import akka.actor.ActorSystem
import akka.testkit.TestKit
import akka.testkit.TestProbe
import models.algorithm.OneDimensionalScore
import models.algorithm.Score
import models.problem.binpacking.localsearch.BinPackingLocalSearch
import models.problem.binpacking.localsearch.BinPackingSolutionHandler
import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.SimpleBinPackingSolution
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.WordSpecLike

import scala.collection.View

class BinPackingLocalSearchExecutorSpec
    extends TestKit(ActorSystem("BinPackingLocalSearchExecutorSpec")) with WordSpecLike with MockFactory
    with BeforeAndAfterAll {

  private val probe = TestProbe()
  private val dumper = probe.ref

  private val executor = new BinPackingLocalSearchExecutor[SimpleBinPackingSolution](dumper)

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "BinPackingLocalSearchExecutor" should {
    "dump intermediate solution steps correctly" when {
      "receiving binPacking with top left start solution, bottom right optimum, and diagonal neighborhood path" in {
        val runId = "runId"

        val boxLength_ = 3
        val box = Box(1, boxLength_)

        val binPacking: BinPackingLocalSearch[SimpleBinPackingSolution] =
          new BinPackingLocalSearch[SimpleBinPackingSolution] {
            override val boxLength: Int = boxLength_
            override val numRectangles: Int = 1
            override val rectangleWidthRange: (Int, Int) = (1, 1)
            override val rectangleHeightRange: (Int, Int) = (1, 1)

            override val solutionHandler: BinPackingSolutionHandler[SimpleBinPackingSolution] =
              new BinPackingSolutionHandler[SimpleBinPackingSolution] {
                override val startSolution: SimpleBinPackingSolution = SimpleBinPackingSolution(
                  Map(rectangles.head -> Placing(box, Coordinates(0, 0)))
                )

                override def getNeighborhood(
                  solution: SimpleBinPackingSolution,
                  step: Int
                ): View[SimpleBinPackingSolution] =
                  Set(
                    Option(
                      SimpleBinPackingSolution.apply(
                        solution.placement.map {
                          case (rectangle, Placing(box, Coordinates(x, y))) =>
                            rectangle -> Placing(box, Coordinates(x + 1, y + 1))
                        }
                      )
                    ).filter(solutionHandler.isFeasible)
                  ).flatten.view

                override def evaluate(solution: SimpleBinPackingSolution, step: Int): Score =
                  solution.placement.head match {
                    case (_, Placing(_, Coordinates(x, y))) => OneDimensionalScore(-(x + y))
                  }
              }
          }

        executor.execute(runId, binPacking)

        probe.expectMsg(
          BinPackingSolutionStep(
            runId,
            0,
            solution = SimpleBinPackingSolution.apply(
              Map(binPacking.rectangles.head -> Placing(box, Coordinates(0, 0)))
            )
          )
        )
        probe.expectMsg(
          BinPackingSolutionStep(
            runId,
            1,
            solution = SimpleBinPackingSolution.apply(
              Map(binPacking.rectangles.head -> Placing(box, Coordinates(1, 1)))
            )
          )
        )
        probe.expectMsg(
          BinPackingSolutionStep(
            runId,
            2,
            solution = SimpleBinPackingSolution.apply(
              Map(binPacking.rectangles.head -> Placing(box, Coordinates(2, 2)))
            )
          )
        )
        probe.expectMsg(
          BinPackingSolutionStep(
            runId,
            3,
            solution = SimpleBinPackingSolution.apply(
              Map(binPacking.rectangles.head -> Placing(box, Coordinates(2, 2)))
            ),
            finished = true
          )
        )
      }
    }
  }

}
