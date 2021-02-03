package dao

import models.problem.rectangles.{Box, Rectangle, RectanglesPlacementSolution}
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.{Completed, Document, MongoDatabase}

import javax.inject.Inject
import scala.concurrent.Future
import scala.jdk.CollectionConverters._

class RectanglesPlacementSolutionDAO @Inject() (val db: MongoDatabase) {

  private lazy val collection = db.getCollection[BsonDocument]("RectanglesPlacementSolutions")

  def dumpSolution(runId: String, step: Int, solution: RectanglesPlacementSolution): Future[Completed] = {
    collection.insertOne(convertSolutionToDocument(runId, step, solution)).toFuture()
  }

  def getSolutionsBetweenSteps(runId: String, stepMin: Int, stepMax: Int): Future[Seq[(String, Int, RectanglesPlacementSolution)]] = {
    collection.find(
      and(
        equal("runId", runId),
        gte("step", stepMin),
        lte("step", stepMax)
      )
    ).map(convertDocumentToSolution).toFuture()
  }

  def convertSolutionToDocument(runId: String, step: Int, solution: RectanglesPlacementSolution): BsonDocument = Document(
    "runId" -> runId,
    "step" -> step,
    "solution" -> solution.placement.toSeq.map {
      case (rectangle, (box, (x, y))) => Document(
        "rectangle" -> Document(
          "id" -> rectangle.id,
          "width" -> rectangle.width,
          "height" -> rectangle.height
        ),
        "box" -> Document(
          "id" -> box.id,
          "width" -> box.width,
          "height" -> box.height
        ),
        "coordinates" -> Document(
          "x" -> x,
          "y" -> y
        )
      )
    }
  ).toBsonDocument

  def convertDocumentToSolution(document: BsonDocument): (String, Int, RectanglesPlacementSolution) = (
    document.get("runId").asString().getValue,
    document.get("step").asInt32().getValue,
    RectanglesPlacementSolution(
      document.get("solution").asArray().asScala.map {
        placingDoc =>
          val rectangleDoc = placingDoc.asDocument().get("rectangle").asDocument()
          val boxDoc = placingDoc.asDocument().get("box").asDocument()
          val coordinatesDoc = placingDoc.asDocument().get("coordinates").asDocument()
          val rectangle = Rectangle(
            rectangleDoc.get("id").asInt32().getValue,
            rectangleDoc.get("width").asInt32().getValue,
            rectangleDoc.get("height").asInt32().getValue
          )
          val box = Box(
            boxDoc.get("id").asInt32().getValue,
            boxDoc.get("width").asInt32().getValue,
            boxDoc.get("height").asInt32().getValue
          )
          val x = coordinatesDoc.get("x").asInt32().getValue
          val y = coordinatesDoc.get("y").asInt32().getValue
          rectangle -> (box, (x, y))
      }.toMap
    )
  )

}
