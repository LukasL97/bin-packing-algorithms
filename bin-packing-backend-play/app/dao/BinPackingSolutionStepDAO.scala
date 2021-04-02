package dao

import org.mongodb.scala.MongoCollection
import org.mongodb.scala.MongoDatabase
import org.mongodb.scala.bson.BsonDocument

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class BinPackingSolutionStepDAO @Inject()(val db: MongoDatabase, implicit val ec: ExecutionContext)
    extends AbstractBinPackingSolutionStepDAO {

  override protected lazy val collection: MongoCollection[BsonDocument] =
    db.getCollection[BsonDocument]("BinPackingSolutionSteps")

}
