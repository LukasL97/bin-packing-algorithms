package dao

import models.problem.rectangles.{Box, Rectangle, RectanglesPlacementSolution}
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.bson.collection.Document
import org.mongodb.scala.{MongoCollection, MongoDatabase}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{MustMatchers, WordSpec}

class RectanglesPlacementSolutionDAOSpec extends WordSpec with MustMatchers with MockFactory {

  val dao = new RectanglesPlacementSolutionDAO(mock[MongoDatabase])

  "RectanglesPlacementSolutionDAO" should {
    "serialize and deserialize correctly" when {
      "given a solution" in {
        val runId = "run"
        val step = 42
        val solution = RectanglesPlacementSolution(
          Map(
            Rectangle(0, 2, 2) -> (Box(0, 4, 4), (1, 1)),
            Rectangle(1, 1, 3) -> (Box(1, 4, 5), (0, 0))
          )
        )
        val document = dao.convertSolutionToDocument(runId, step, solution)
        val (actualRunId, actualStep, actualSolution) = dao.convertDocumentToSolution(document)
        actualRunId mustEqual runId
        actualStep mustEqual step
        actualSolution mustEqual solution
      }
    }
  }

}
