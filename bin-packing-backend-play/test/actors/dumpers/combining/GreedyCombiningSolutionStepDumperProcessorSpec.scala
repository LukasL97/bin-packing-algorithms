package actors.dumpers.combining

import actors.BinPackingSolutionStep
import dao.CombinedBinPackingSolutionStepDAO
import models.problem.binpacking.BinPackingInstance
import models.problem.binpacking.solution.SimpleBinPackingSolution
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterEach
import org.scalatest.MustMatchers
import org.scalatest.WordSpec

class GreedyCombiningSolutionStepDumperProcessorSpec
    extends WordSpec with MustMatchers with MockFactory with BeforeAndAfterEach {

  private val numRectangles = 500

  private val instance = BinPackingInstance(10, numRectangles, 1, 4, 1, 4)
  private val dumper = new GreedyCombiningSolutionStepDumperProcessor(mock[CombinedBinPackingSolutionStepDAO], instance)

  private def createSolutionStep(step: Int, finished: Boolean = false) = BinPackingSolutionStep(
    "runId",
    step,
    SimpleBinPackingSolution.apply(10),
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

      "last step in queue is start step" in {
        val startStep = createSolutionStep(0)
        dumper.queue.append(startStep)
        val poppedSteps = dumper.popStepsFromQueue()
        poppedSteps mustEqual Seq(startStep)
        dumper.queue must be(empty)
      }

      "last step in queue is finished" in {
        val solutionSteps = (1 to 3).map(createSolutionStep(_)).appended(createSolutionStep(4, finished = true))
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
  }

}
