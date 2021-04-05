package dao

import actors.BinPackingSolutionStep
import metrics.Metrics
import org.mongodb.scala.Completed
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.MongoDatabase
import org.mongodb.scala.bson.BsonArray
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.model.Filters.and
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Filters.gte
import org.mongodb.scala.model.Filters.lte
import utils.SerializationUtil

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import utils.BinPackingSolutionRepresentationSerializationUtil.formats

trait AbstractBinPackingSolutionStepDAO extends Metrics {

  val db: MongoDatabase
  implicit val ec: ExecutionContext

  protected val collection: MongoCollection[BsonDocument]

  def dumpSolutionStep(solutionStep: BinPackingSolutionStep): Future[Completed] = {
    withTimer("dump-solution-step", "runId" -> solutionStep.runId) {
      collection.insertOne(convertSolutionStepToDocument(solutionStep)).toFuture()
    }
  }

  def getSolutionStepsInStepRange(runId: String, stepMin: Int, stepMax: Int): Future[Seq[BinPackingSolutionStep]] = {
    collection
      .find(
        and(
          equal("runId", runId),
          gte("step", stepMin),
          lte("step", stepMax)
        )
      )
      .map(convertDocumentToSolutionStep)
      .toFuture()
      .map(steps => steps.sortBy(_.step))
  }

  def getRawSolutionsStepsInStepRange(runId: String, stepMin: Int, stepMax: Int): Future[BsonArray] = {
    collection
      .find(
        and(
          equal("runId", runId),
          gte("step", stepMin),
          lte("step", stepMax)
        )
      )
      .toFuture()
      .map(_.sortBy(_.get("step").asInt32()))
      .map(BsonArray.fromIterable)
  }

  def convertSolutionStepToDocument(solutionStep: BinPackingSolutionStep): BsonDocument = {
    withTimer("convert-solution-step-to-document", "runId" -> solutionStep.runId) {
      BsonSerializationUtil.solutionStepToDocument(solutionStep)
    }
  }

  def convertDocumentToSolutionStep(document: BsonDocument): BinPackingSolutionStep = {
    withTimer("convert-document-to-solution-step") {
      SerializationUtil.fromJsonString[BinPackingSolutionStep](document.toJson)
    }
  }

}
