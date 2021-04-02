package modules

import actors.BinPackingActor
import actors.dumpers.SolutionStepDumper
import com.google.inject.AbstractModule
import org.mongodb.scala.MongoClient
import org.mongodb.scala.MongoDatabase
import play.api.libs.concurrent.AkkaGuiceSupport

class Module extends AbstractModule with AkkaGuiceSupport {

  override def configure(): Unit = {
    bind(classOf[MongoDatabase]).toInstance(getDB)
    bindActorFactory[BinPackingActor, BinPackingActor.Factory]
    bindActorFactory[SolutionStepDumper, SolutionStepDumper.Factory]
  }

  private def getDB: MongoDatabase = {
    val uri = sys.env.getOrElse("MONGODB_URI", "mongodb://localhost:27017")
    val client = MongoClient(uri)
    println(s"Setup connection to MongoClient at $uri")
    client.getDatabase("BinPacking")
  }

}
