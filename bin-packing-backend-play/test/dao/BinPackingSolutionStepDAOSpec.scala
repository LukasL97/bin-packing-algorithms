package dao

import actors.BinPackingSolutionStep
import models.problem.binpacking.SimpleBinPackingSolution
import models.problem.binpacking.Box
import models.problem.binpacking.Coordinates
import models.problem.binpacking.Placing
import models.problem.binpacking.Rectangle
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
          SimpleBinPackingSolution(
            Map(
              Rectangle(0, 2, 2) -> Placing(Box(0, 4), Coordinates(1, 1)),
              Rectangle(1, 1, 3) -> Placing(Box(1, 4), Coordinates(0, 0))
            )
          )
        )
        val document = dao.convertSolutionStepToDocument(solutionStep)
        dao.convertDocumentToSolutionStep(document) mustEqual solutionStep
      }
    }
  }

}
