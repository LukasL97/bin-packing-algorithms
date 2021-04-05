package actors.dumpers.combining

import actors.BinPackingSolutionStep
import dao.CombinedBinPackingSolutionStepDAO
import models.problem.binpacking.BinPackingInstance
import models.problem.binpacking.solution.SimpleBinPackingSolutionRepresentation
import models.problem.binpacking.solution.update.StartSolution
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterEach
import org.scalatest.MustMatchers
import org.scalatest.WordSpec

class GreedyCombiningSolutionStepDumperProcessorSpec
    extends WordSpec with MustMatchers with MockFactory with BeforeAndAfterEach {

  private val numRectangles = 500

  private val instance = BinPackingInstance(10, numRectangles, 1, 4, 1, 4)
  private val dao = mock[CombinedBinPackingSolutionStepDAO]
  private val dumper = new GreedyCombiningSolutionStepDumperProcessor(dao, instance)

  private def createSolutionStep(step: Int, finished: Boolean = false) = BinPackingSolutionStep(
    "runId",
    step,
    SimpleBinPackingSolutionRepresentation(Map.empty, StartSolution()),
    finished
  )

  override def beforeEach(): Unit = {
    dumper.queue.clear()
  }

  "GreedyCombiningSolutionStepDumperProcessor" should {

    "pop steps from queue" when {
      "enough steps in queue to pop" in {
        val solutionSteps = (1 to 5).map(createSolutionStep(_))
        dumper.queue.appendAll(solutionSteps)
        val poppedSteps = dumper.popStepsFromQueue()
        poppedSteps mustEqual solutionSteps
        dumper.queue must be(empty)
      }
    }

    "not pop any steps from queue" when {
      "not enough steps in queue to pop" in {
        val solutionSteps = (1 to 4).map(createSolutionStep(_))
        dumper.queue.appendAll(solutionSteps)
        val poppedSteps = dumper.popStepsFromQueue()
        poppedSteps must be(empty)
        dumper.queue.toSeq mustEqual solutionSteps
      }
    }

    "dump solution step immediately" when {
      "given a start step" in {
        val solutionStep = createSolutionStep(0)
        (dao.dumpSolutionStep _).expects(solutionStep)
        dumper.process(solutionStep)
        dumper.queue must be(empty)
      }
      "given a finished step" in {
        val solutionStep = createSolutionStep(42, finished = true)
        (dao.dumpSolutionStep _).expects(solutionStep)
        dumper.process(solutionStep)
        dumper.queue must be(empty)
      }
    }
  }

}
