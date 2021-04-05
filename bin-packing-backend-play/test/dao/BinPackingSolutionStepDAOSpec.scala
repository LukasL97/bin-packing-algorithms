package dao

import actors.BinPackingSolutionStep
import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.RectanglePermutationBinPackingSolutionRepresentation
import models.problem.binpacking.solution.SimpleBinPackingSolutionRepresentation
import models.problem.binpacking.solution.update.RectanglesChanged
import models.problem.binpacking.solution.update.StartSolution
import org.mongodb.scala.MongoDatabase
import org.scalamock.scalatest.MockFactory
import org.scalatest.MustMatchers
import org.scalatest.WordSpec

import scala.concurrent.ExecutionContext

class BinPackingSolutionStepDAOSpec extends WordSpec with MustMatchers with MockFactory {

  val dao = new BinPackingSolutionStepDAO(mock[MongoDatabase], mock[ExecutionContext])

  "BinPackingSolutionStepDAO" should {
    "serialize and deserialize correctly" when {
      "given a solution step" in {
        val solutionStep = BinPackingSolutionStep(
          "run",
          42,
          SimpleBinPackingSolutionRepresentation(
            Map(
              Rectangle(0, 2, 2) -> Placing(Box(0, 4), Coordinates(1, 1)),
              Rectangle(1, 1, 3) -> Placing(Box(1, 4), Coordinates(0, 0))
            ),
            StartSolution()
          )
        )
        val document = dao.convertSolutionStepToDocument(solutionStep)
        dao.convertDocumentToSolutionStep(document) mustEqual solutionStep
      }
      "given a solution step with rectangle permutation representation" in {
        val solutionStep = BinPackingSolutionStep(
          "run",
          1337,
          RectanglePermutationBinPackingSolutionRepresentation(
            Map(
              Rectangle(0, 2, 2) -> Placing(Box(0, 4), Coordinates(1, 1)),
              Rectangle(1, 1, 3) -> Placing(Box(1, 4), Coordinates(0, 0)),
              Rectangle(2, 4, 4) -> Placing(Box(2, 4), Coordinates(0, 0))
            ),
            RectanglesChanged(Set(0, 1)),
            Seq(0, 2, 1)
          )
        )
        val document = dao.convertSolutionStepToDocument(solutionStep)
        dao.convertDocumentToSolutionStep(document) mustEqual solutionStep
      }
    }
  }

}
