package actors.executors

import actors.BinPackingSolutionStep
import akka.actor.ActorSystem
import akka.testkit.TestKit
import akka.testkit.TestProbe
import models.problem.binpacking.BinPackingInstance
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
    override val instance: BinPackingInstance
  ) extends BasicBinPackingGreedy {
    override val selectionHandler = new BinPackingSelectionHandlerImpl(instance.boxLength, instance.rectangles)
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

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "BinPackingGreedyExecutor" should {
    "dump intermediate solution steps correctly" when {
      "receiving a binPacking with three rectangles to place" in {

        val boxLength = 6
        val numRectangles = 3
        val minWidth = 3
        val maxWidth = 3
        val minHeight = 2
        val maxHeight = 2

        val instance = BinPackingInstance(
          boxLength,
          numRectangles,
          minWidth,
          maxWidth,
          minHeight,
          maxHeight
        )

        val runId = "runId"
        val binPacking = new BinPackingGreedyImpl(instance)

        val executor = new BinPackingGreedyExecutor(binPacking, runId, Seq(dumper))
        executor.execute()

        val box = Box(1, boxLength)

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
                instance.rectangles.toSeq.head -> Placing(box, Coordinates(0, 0))
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
                instance.rectangles.toSeq.head -> Placing(box, Coordinates(0, 0)),
                instance.rectangles.toSeq(1) -> Placing(box, Coordinates(0, 2))
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
                instance.rectangles.toSeq.head -> Placing(box, Coordinates(0, 0)),
                instance.rectangles.toSeq(1) -> Placing(box, Coordinates(0, 2)),
                instance.rectangles.toSeq(2) -> Placing(box, Coordinates(3, 0))
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
                instance.rectangles.toSeq.head -> Placing(box, Coordinates(0, 0)),
                instance.rectangles.toSeq(1) -> Placing(box, Coordinates(0, 2)),
                instance.rectangles.toSeq(2) -> Placing(box, Coordinates(3, 0))
              )
            ),
            finished = true
          )
        )
      }
    }
  }

}
