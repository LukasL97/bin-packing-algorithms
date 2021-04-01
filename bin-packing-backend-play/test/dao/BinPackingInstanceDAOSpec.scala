package dao

import models.problem.binpacking.BinPackingInstance
import org.mongodb.scala.MongoDatabase
import org.scalamock.scalatest.MockFactory
import org.scalatest.MustMatchers
import org.scalatest.WordSpec

import scala.concurrent.ExecutionContext

class BinPackingInstanceDAOSpec extends WordSpec with MustMatchers with MockFactory {

  val dao = new BinPackingInstanceDAO(mock[MongoDatabase], mock[ExecutionContext])

  "BinPackingInstanceDAO" should {
    "serialize and deserialize instances" in {
      val instance = BinPackingInstance(10, 100, 1, 4, 1, 4)
      val document = dao.convertInstanceToDocument(instance)
      dao.convertDocumentToInstance(document) mustEqual instance
    }
  }

}
