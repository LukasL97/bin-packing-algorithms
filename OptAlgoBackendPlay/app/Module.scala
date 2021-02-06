import actors.RectanglesPlacementActor
import com.google.inject.AbstractModule
import org.mongodb.scala.MongoClient
import org.mongodb.scala.MongoDatabase
import play.api.libs.concurrent.AkkaGuiceSupport

class Module extends AbstractModule with AkkaGuiceSupport {

  override def configure(): Unit = {
    bind(classOf[MongoDatabase]).toInstance(getDB)
    bindActorFactory[RectanglesPlacementActor, RectanglesPlacementActor.Factory]
  }

  private def getDB: MongoDatabase = {
    val client = MongoClient("mongodb://localhost:27017")
    client.getDatabase("OptAlgo")
  }

}
