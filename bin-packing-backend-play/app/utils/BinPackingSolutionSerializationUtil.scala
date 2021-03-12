package utils

import models.problem.binpacking.BinPackingSolution
import models.problem.binpacking.Box
import models.problem.binpacking.Coordinates
import models.problem.binpacking.Placing
import models.problem.binpacking.Rectangle
import models.problem.binpacking.SimpleBinPackingSolution
import org.json4s
import org.json4s.Formats
import org.json4s.JsonAST.JArray
import org.json4s.JsonAST.JInt
import org.json4s.JsonAST.JObject
import org.json4s.JsonAST.JValue
import org.json4s.ShortTypeHints

object BinPackingSolutionSerializationUtil {

  implicit val formats: Formats = SerializationUtil.defaultFormats(
    new ShortTypeHints(List(classOf[SimpleBinPackingSolution])) {

      override def serialize: PartialFunction[Any, json4s.JObject] = {
        case solution: SimpleBinPackingSolution => solutionToJObject(solution)
      }

      override def deserialize: PartialFunction[(String, json4s.JObject), Any] = {
        case ("SimpleBinPackingSolution", jObject) => jObjectToSolution(jObject)
      }
    }
  )

  private def solutionToJObject(solution: BinPackingSolution): JObject = JObject(
    "placement" -> JArray(
      solution.placement.toSeq.map {
        case (rectangle, Placing(box, Coordinates(x, y))) =>
          SerializationUtil.toJson(
            Map(
              "rectangle" -> SerializationUtil.toJson(rectangle),
              "box" -> SerializationUtil.toJson(box),
              "coordinates" -> Map(
                "x" -> x,
                "y" -> y
              )
            )
          )
      }.toList
    )
  )

  private def getFieldValue(jObject: JObject, key: String): JValue = {
    jObject.obj.collect { case (key_, value) if key_ == key => value }.head
  }

  private def jObjectToSolution(jObject: JObject): SimpleBinPackingSolution = SimpleBinPackingSolution(
    getFieldValue(jObject, "placement")
      .asInstanceOf[JArray]
      .arr
      .map(_.asInstanceOf[JObject])
      .map { placing =>
        val rectangle = SerializationUtil.fromJson[Rectangle](getFieldValue(placing, "rectangle"))
        val box = SerializationUtil.fromJson[Box](getFieldValue(placing, "box"))
        val coordinates = getFieldValue(placing, "coordinates").asInstanceOf[JObject]
        val x = getFieldValue(coordinates, "x").asInstanceOf[JInt].num.toInt
        val y = getFieldValue(coordinates, "y").asInstanceOf[JInt].num.toInt
        rectangle -> Placing(box, Coordinates(x, y))
      }
      .toMap
  )

}
