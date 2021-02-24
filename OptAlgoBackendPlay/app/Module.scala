import actors.RectanglesPlacementActor
import com.google.inject.AbstractModule
import org.mongodb.scala.MongoClient
import org.mongodb.scala.MongoDatabase
import play.api.Logging
import play.api.libs.concurrent.AkkaGuiceSupport

class Module extends AbstractModule with AkkaGuiceSupport with Logging {

  override def configure(): Unit = {
    bind(classOf[MongoDatabase]).toInstance(getDB)
    bindActorFactory[RectanglesPlacementActor, RectanglesPlacementActor.Factory]
  }

  private def getDB: MongoDatabase = {
    val host = sys.env.getOrElse("MONGODB_HOST", "localhost")
    val port = sys.env.getOrElse("MONGODB_PORT", "27017")
    val uri = s"mongodb://$host:$port"
    val client = MongoClient(uri)
    println(s"Setup connection to MongoClient at $uri")
    client.getDatabase("OptAlgo")
  }

}
