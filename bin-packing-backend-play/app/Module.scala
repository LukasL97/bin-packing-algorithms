import actors.BinPackingActor
import com.google.inject.AbstractModule
import org.mongodb.scala.MongoClient
import org.mongodb.scala.MongoDatabase
import play.api.libs.concurrent.AkkaGuiceSupport

class Module extends AbstractModule with AkkaGuiceSupport {

  override def configure(): Unit = {
    bind(classOf[MongoDatabase]).toInstance(getDB)
    bindActorFactory[BinPackingActor, BinPackingActor.Factory]
  }

  private def getDB: MongoDatabase = {
    val host = sys.env.getOrElse("MONGODB_HOST", "localhost")
    val port = sys.env.getOrElse("MONGODB_PORT", "27017")
    val uri = s"mongodb://$host:$port"
    val client = MongoClient(uri)
    println(s"Setup connection to MongoClient at $uri")
    client.getDatabase("BinPacking")
  }

}
