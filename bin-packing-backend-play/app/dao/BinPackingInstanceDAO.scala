package dao

import models.problem.binpacking.BinPackingInstance
import org.mongodb.scala.Completed
import org.mongodb.scala.MongoDatabase
import org.mongodb.scala.bson.BsonDocument
import utils.SerializationUtil

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class BinPackingInstanceDAO @Inject()(val db: MongoDatabase, implicit val ec: ExecutionContext) {

  private lazy val collection = db.getCollection[BsonDocument]("BinPackingInstances")

  def dumpInstance(instance: BinPackingInstance): Future[Completed] = {
    collection.insertOne(convertInstanceToDocument(instance)).toFuture()
  }

  def convertInstanceToDocument(instance: BinPackingInstance): BsonDocument = {
    BsonDocument(SerializationUtil.toJsonString(instance))
  }

  def convertDocumentToInstance(document: BsonDocument): BinPackingInstance = {
    SerializationUtil.fromJsonString[BinPackingInstance](document.toJson)
  }

}
