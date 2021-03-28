package actors.executors

import actors.BinPackingSolutionStep
import akka.actor.ActorSystem
import akka.testkit.TestKit
import akka.testkit.TestProbe
import models.problem.binpacking.greedy.BinPackingGreedy
import models.problem.binpacking.greedy.BinPackingSelectionHandler
import models.problem.binpacking.greedy.basic.BasicBinPackingGreedy
import models.problem.binpacking.greedy.basic.BasicBinPackingSelectionHandler
import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.SimpleBinPackingSolution
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.WordSpecLike

class BinPackingGreedyExecutorSpec
    extends TestKit(ActorSystem("BinPackingGreedyExecutorSpec")) with WordSpecLike with MockFactory
    with BeforeAndAfterAll {

  private class BinPackingGreedyImpl(
    override val boxLength: Int,
    override val numRectangles: Int,
    override val rectangleWidthRange: (Int, Int),
    override val rectangleHeightRange: (Int, Int)
  ) extends BasicBinPackingGreedy {
    override val selectionHandler = new BinPackingSelectionHandlerImpl(boxLength, rectangles)
  }

  private class BinPackingSelectionHandlerImpl(
    override val boxLength: Int,
    override val candidates: Iterable[Rectangle]
  ) extends BasicBinPackingSelectionHandler {
    override def selectNextCandidate(
      candidates: Iterable[Rectangle],
      solution: SimpleBinPackingSolution
    ): (Rectangle, Iterable[Rectangle]) = {
      (candidates.head, candidates.tail)
    }
  }

  private val probe = TestProbe()
  private val dumper = probe.ref

  private val executor = new BinPackingGreedyExecutor[SimpleBinPackingSolution](dumper)

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "BinPackingGreedyExecutor" should {
    "dump intermediate solution steps correctly" when {
      "receiving a binPacking with three rectangles to place" in {
        val runId = "runId"

        val boxLength = 6
        val numRectangles = 3
        val rectanglesWidthRange = (3, 3)
        val rectanglesHeightRange = (2, 2)

        val box = Box(1, boxLength)

        val binPacking = new BinPackingGreedyImpl(
          boxLength,
          numRectangles,
          rectanglesWidthRange,
          rectanglesHeightRange
        )

        executor.execute(runId, binPacking)

        probe.expectMsg(
          BinPackingSolutionStep(
            runId,
            0,
            SimpleBinPackingSolution.apply(
              boxLength
            )
          )
        )
        probe.expectMsg(
          BinPackingSolutionStep(
            runId,
            1,
            SimpleBinPackingSolution.apply(
              Map(
                binPacking.rectangles.toSeq.head -> Placing(box, Coordinates(0, 0))
              )
            )
          )
        )
        probe.expectMsg(
          BinPackingSolutionStep(
            runId,
            2,
            SimpleBinPackingSolution.apply(
              Map(
                binPacking.rectangles.toSeq.head -> Placing(box, Coordinates(0, 0)),
                binPacking.rectangles.toSeq(1) -> Placing(box, Coordinates(0, 2))
              )
            )
          )
        )
        probe.expectMsg(
          BinPackingSolutionStep(
            runId,
            3,
            SimpleBinPackingSolution.apply(
              Map(
                binPacking.rectangles.toSeq.head -> Placing(box, Coordinates(0, 0)),
                binPacking.rectangles.toSeq(1) -> Placing(box, Coordinates(0, 2)),
                binPacking.rectangles.toSeq(2) -> Placing(box, Coordinates(3, 0))
              )
            )
          )
        )
        probe.expectMsg(
          BinPackingSolutionStep(
            runId,
            4,
            SimpleBinPackingSolution.apply(
              Map(
                binPacking.rectangles.toSeq.head -> Placing(box, Coordinates(0, 0)),
                binPacking.rectangles.toSeq(1) -> Placing(box, Coordinates(0, 2)),
                binPacking.rectangles.toSeq(2) -> Placing(box, Coordinates(3, 0))
              )
            ),
            finished = true
          )
        )
      }
    }
  }

}
