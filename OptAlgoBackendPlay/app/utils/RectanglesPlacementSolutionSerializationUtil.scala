package utils

import models.problem.rectangles.{Box, Rectangle, RectanglesPlacementSolution}
import org.json4s
import org.json4s.JsonAST.{JArray, JInt, JObject, JValue}
import org.json4s.{Formats, ShortTypeHints}

object RectanglesPlacementSolutionSerializationUtil {

  implicit val formats: Formats = SerializationUtil.defaultFormats(
    new ShortTypeHints(List(classOf[RectanglesPlacementSolution])) {

      override def serialize: PartialFunction[Any, json4s.JObject] = {
        case solution: RectanglesPlacementSolution => solutionToJObject(solution)
      }

      override def deserialize: PartialFunction[(String, json4s.JObject), Any] = {
        case ("RectanglesPlacementSolution", jObject) => jObjectToSolution(jObject)
      }
    }
  )

  private def solutionToJObject(solution: RectanglesPlacementSolution): JObject = JObject(
    "placement" -> JArray(
      solution.placement.toSeq.map {
        case (rectangle, (box, (x, y))) => SerializationUtil.toJson(Map(
          "rectangle" -> SerializationUtil.toJson(rectangle),
          "box" -> SerializationUtil.toJson(box),
          "coordinates" -> Map(
            "x" -> x,
            "y" -> y
          )
        ))
      }.toList
    )
  )

  private def getFieldValue(jObject: JObject, key: String): JValue = {
    jObject.obj
      .collect { case (key_, value) if key_ == key => value }
      .head
  }

  private def jObjectToSolution(jObject: JObject): RectanglesPlacementSolution = RectanglesPlacementSolution(
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
        rectangle -> (box, (x, y))
      }.toMap
  )

}
