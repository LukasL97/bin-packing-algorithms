import com.google.inject.AbstractModule
import org.mongodb.scala.{MongoClient, MongoDatabase}

class Module extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[MongoDatabase]).toInstance(getDB)
  }

  private def getDB: MongoDatabase = {
    val client = MongoClient("mongodb://localhost:27017")
    client.getDatabase("OptAlgo")
  }
}
