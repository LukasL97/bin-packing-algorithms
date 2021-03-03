package dao

import actors.BinPackingSolutionStep
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.Completed
import org.mongodb.scala.MongoDatabase
import utils.BinPackingSolutionSerializationUtil.formats
import utils.SerializationUtil

import javax.inject.Inject
import scala.concurrent.Future

class BinPackingSolutionStepDAO @Inject()(val db: MongoDatabase) {

  private lazy val collection = db.getCollection[BsonDocument]("BinPackingSolutionsSteps")

  def dumpSolutionStep(solutionStep: BinPackingSolutionStep): Future[Completed] = {
    collection.insertOne(convertSolutionStepToDocument(solutionStep)).toFuture()
  }

  def getSolutionStepsInStepRange(runId: String, stepMin: Int, stepMax: Int): Future[Seq[BinPackingSolutionStep]] = {
    collection.find(
      and(
        equal("runId", runId),
        gte("step", stepMin),
        lte("step", stepMax)
      )
    ).map(convertDocumentToSolutionStep).toFuture()
  }

  def convertSolutionStepToDocument(solutionStep: BinPackingSolutionStep): BsonDocument = {
    BsonDocument(SerializationUtil.toJsonString(solutionStep))
  }

  def convertDocumentToSolutionStep(document: BsonDocument): BinPackingSolutionStep = {
    SerializationUtil.fromJsonString[BinPackingSolutionStep](document.toJson)
  }

}