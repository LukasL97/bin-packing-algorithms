package dao

import actors.RectanglesPlacementSolutionStep
import models.problem.rectangles.Coordinates
import models.problem.rectangles.Placing
import models.problem.rectangles.{Box, Rectangle, RectanglesPlacementSolution}
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.bson.collection.Document
import org.mongodb.scala.{MongoCollection, MongoDatabase}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{MustMatchers, WordSpec}

class RectanglesPlacementSolutionStepDAOSpec extends WordSpec with MustMatchers with MockFactory {

  val dao = new RectanglesPlacementSolutionStepDAO(mock[MongoDatabase])

  "RectanglesPlacementSolutionStepDAO" should {
    "serialize and deserialize correctly" when {
      "given a solution step" in {
        val solutionStep = RectanglesPlacementSolutionStep(
          "run",
          42,
          RectanglesPlacementSolution(
            Map(
              Rectangle(0, 2, 2) -> Placing(Box(0, 4, 4), Coordinates(1, 1)),
              Rectangle(1, 1, 3) -> Placing(Box(1, 4, 5), Coordinates(0, 0))
            )
          )
        )
        val document = dao.convertSolutionStepToDocument(solutionStep)
        dao.convertDocumentToSolutionStep(document) mustEqual solutionStep
      }
    }
  }

}
