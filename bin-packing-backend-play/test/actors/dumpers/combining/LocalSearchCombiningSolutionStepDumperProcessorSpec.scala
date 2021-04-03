package actors.dumpers.combining

import actors.BinPackingSolutionStep
import dao.CombinedBinPackingSolutionStepDAO
import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.SimpleBinPackingSolution
import models.problem.binpacking.solution.update.RectanglesChanged
import models.problem.binpacking.solution.update.UnchangedSolution
import org.scalamock.scalatest.MockFactory
import org.scalatest.MustMatchers
import org.scalatest.WordSpec

class LocalSearchCombiningSolutionStepDumperProcessorSpec extends WordSpec with MustMatchers with MockFactory {

  private val dao = mock[CombinedBinPackingSolutionStepDAO]
  val dumper = new LocalSearchCombiningSolutionStepDumperProcessor(dao)

  private def createUnchangedSolutionStep(step: Int, finished: Boolean = false) = BinPackingSolutionStep(
    "runId",
    step,
    new SimpleBinPackingSolution(
      Map.empty,
      UnchangedSolution()
    ),
    finished
  )

  private def createMinimalRectangleChangedSolutionStep(step: Int, finished: Boolean = false) = BinPackingSolutionStep(
    "runId",
    step,
    new SimpleBinPackingSolution(
      Map.empty,
      RectanglesChanged(Set(step))
    ),
    finished
  )

  "LocalSearchCombiningSolutionStepDumperProcessor" should {

    "enqueue without dumping any steps" when {
      "adding another minimal step to a queue of minimal steps with the threshold not reached" in {
        (1 to 7).foreach(i => dumper.queue.append(createUnchangedSolutionStep(i)))
        dumper.process(createUnchangedSolutionStep(8))
        dumper.queue.toSeq mustEqual (1 to 8).map(createUnchangedSolutionStep(_))
      }
    }

    "dump combined steps" when {
      "adding another minimal step to a queue of minimal steps with the threshold reached" in {
        (1 until dumper.minimalChangesCombiningThreshold)
          .foreach(i => dumper.queue.append(createUnchangedSolutionStep(i)))
        (dao.dumpSolutionStep _).expects(createUnchangedSolutionStep(dumper.minimalChangesCombiningThreshold))
        dumper.process(createUnchangedSolutionStep(dumper.minimalChangesCombiningThreshold))
        dumper.queue must be(empty)
      }

      "adding another minimal step to a queue of diverse minimal steps" in {
        (1 to 7).foreach(i => dumper.queue.append(createUnchangedSolutionStep(i)))
        (8 until dumper.minimalChangesCombiningThreshold)
          .foreach(i => dumper.queue.append(createMinimalRectangleChangedSolutionStep(i)))
        (dao.dumpSolutionStep _).expects(BinPackingSolutionStep(
          "runId",
          dumper.minimalChangesCombiningThreshold,
          new SimpleBinPackingSolution(
            Map.empty,
            RectanglesChanged((8 until dumper.minimalChangesCombiningThreshold).toSet)
          )
        ))
        dumper.process(createUnchangedSolutionStep(dumper.minimalChangesCombiningThreshold))
        dumper.queue must be(empty)
      }
    }

    "dump combined minimal steps and a new non-minimal step" when {
      "a non-minimal step is added to the queue" in {
        (1 to 7).foreach(i => dumper.queue.append(createUnchangedSolutionStep(i)))
        val newStep = BinPackingSolutionStep(
          "runId",
          8,
          new SimpleBinPackingSolution(
            Map(
              Rectangle(1, 1, 1) -> Placing(Box(1, 10), Coordinates(0, 0)),
              Rectangle(2, 1, 1) -> Placing(Box(1, 10), Coordinates(1, 0))
            ),
            RectanglesChanged(Set(1, 2))
          )
        )
        (dao.dumpSolutionStep _).expects(createUnchangedSolutionStep(7))
        (dao.dumpSolutionStep _).expects(newStep)
        dumper.process(newStep)
        dumper.queue must be(empty)
      }
    }
  }

}
