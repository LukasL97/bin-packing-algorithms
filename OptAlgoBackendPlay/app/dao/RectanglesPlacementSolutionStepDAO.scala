package dao

import actors.RectanglesPlacementSolutionStep
import models.problem.rectangles.{Box, Rectangle, RectanglesPlacementSolution}
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.{Completed, Document, MongoDatabase}
import utils.SerializationUtil
import utils.RectanglesPlacementSolutionSerializationUtil.formats

import javax.inject.Inject
import scala.concurrent.Future
import scala.jdk.CollectionConverters._

class RectanglesPlacementSolutionStepDAO @Inject()(val db: MongoDatabase) {

  private lazy val collection = db.getCollection[BsonDocument]("RectanglesPlacementSolutionsSteps")

  def dumpSolutionStep(solutionStep: RectanglesPlacementSolutionStep): Future[Completed] = {
    collection.insertOne(convertSolutionStepToDocument(solutionStep)).toFuture()
  }

  def getSolutionStepsInStepRange(runId: String, stepMin: Int, stepMax: Int): Future[Seq[RectanglesPlacementSolutionStep]] = {
    collection.find(
      and(
        equal("runId", runId),
        gte("step", stepMin),
        lte("step", stepMax)
      )
    ).map(convertDocumentToSolutionStep).toFuture()
  }

  def convertSolutionStepToDocument(solutionStep: RectanglesPlacementSolutionStep): BsonDocument = {
    BsonDocument(SerializationUtil.toJsonString(solutionStep))
  }

  def convertDocumentToSolutionStep(document: BsonDocument): RectanglesPlacementSolutionStep = {
    SerializationUtil.fromJsonString[RectanglesPlacementSolutionStep](document.toJson)
  }

}
