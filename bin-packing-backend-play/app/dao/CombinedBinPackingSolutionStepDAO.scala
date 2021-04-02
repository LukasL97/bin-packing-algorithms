package dao

import actors.BinPackingSolutionStep
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.MongoDatabase
import org.mongodb.scala.bson.BsonDocument

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class CombinedBinPackingSolutionStepDAO @Inject()(val db: MongoDatabase, implicit val ec: ExecutionContext)
  extends AbstractBinPackingSolutionStepDAO {

  override protected lazy val collection: MongoCollection[BsonDocument] =
    db.getCollection[BsonDocument]("CombinedBinPackingSolutionSteps")

  def getSolutionStepsFromStep(runId: String, step: Int): Future[Seq[BinPackingSolutionStep]] = {
    getSolutionStepsInStepRange(runId, step, Int.MaxValue)
  }

}