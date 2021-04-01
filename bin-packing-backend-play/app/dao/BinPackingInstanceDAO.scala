package dao

import models.problem.binpacking.BinPackingInstance
import org.mongodb.scala.Completed
import org.mongodb.scala.MongoDatabase
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.model.Filters.equal
import utils.SerializationUtil

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class BinPackingInstanceDAO @Inject()(val db: MongoDatabase, implicit val ec: ExecutionContext) {

  private lazy val collection = db.getCollection[BsonDocument]("BinPackingInstances")

  def dumpInstance(instance: BinPackingInstance): Future[Completed] = {
    collection.insertOne(convertInstanceToDocument(instance)).toFuture()
  }

  def getInstance(id: String): Future[BinPackingInstance] = {
    collection
      .find(
        equal("id", id)
      )
      .headOption()
      .map(_.getOrElse(throw new RuntimeException(s"Could not find instance with id $id in database")))
      .map(convertDocumentToInstance)
  }

  def getAllInstances: Future[Seq[BinPackingInstance]] = {
    collection
      .find()
      .toFuture()
      .map(documents => documents.map(convertDocumentToInstance))
  }

  def convertInstanceToDocument(instance: BinPackingInstance): BsonDocument = {
    BsonDocument(SerializationUtil.toJsonString(instance))
  }

  def convertDocumentToInstance(document: BsonDocument): BinPackingInstance = {
    SerializationUtil.fromJsonString[BinPackingInstance](document.toJson)
  }

}
