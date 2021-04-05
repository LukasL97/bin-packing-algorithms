package utils

import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.RectanglePermutationBinPackingSolutionRepresentation
import models.problem.binpacking.solution.SimpleBinPackingSolutionRepresentation
import models.problem.binpacking.solution.update.BoxOrderChanged
import models.problem.binpacking.solution.update.RectanglesChanged
import models.problem.binpacking.solution.update.StartSolution
import models.problem.binpacking.solution.update.UnchangedSolution
import models.problem.binpacking.solution.update.Update
import models.problem.binpacking.utils.TopLeftFirstCoordinateOrdering
import org.json4s
import org.json4s.Formats
import org.json4s.JsonAST.JArray
import org.json4s.JsonAST.JInt
import org.json4s.JsonAST.JObject
import org.json4s.JsonAST.JValue
import org.json4s.ShortTypeHints

import scala.collection.SortedSet

object BinPackingSolutionRepresentationSerializationUtil extends TopLeftFirstCoordinateOrdering {

  implicit val formats: Formats = SerializationUtil.defaultFormats(
    new ShortTypeHints(
      List(
        classOf[SimpleBinPackingSolutionRepresentation],
        classOf[RectanglePermutationBinPackingSolutionRepresentation],
        classOf[StartSolution],
        classOf[RectanglesChanged],
        classOf[BoxOrderChanged],
        classOf[UnchangedSolution]
      )
    ) {

      override def serialize: PartialFunction[Any, json4s.JObject] = {
        case solution: SimpleBinPackingSolutionRepresentation => simpleSolutionToJObject(solution)
        case solution: RectanglePermutationBinPackingSolutionRepresentation => topLeftFirstSolutionToJObject(solution)
        case _: StartSolution => JObject()
        case update: RectanglesChanged => JObject("rectangleIds" -> SerializationUtil.toJson(update.rectangleIds))
        case _: BoxOrderChanged => JObject()
        case _: UnchangedSolution => JObject()
      }

      override def deserialize: PartialFunction[(String, json4s.JObject), Any] = {
        case ("SimpleBinPackingSolutionRepresentation", jObject) => jObjectToSimpleSolution(jObject)
        case ("RectanglePermutationBinPackingSolutionRepresentation", jObject) =>
          jObjectToRectanglePermutationSolution(jObject)
        case ("StartSolution", _) => StartSolution()
        case ("RectanglesChanged", jObject) =>
          RectanglesChanged(
            getFieldValue(jObject, "rectangleIds").asInstanceOf[JArray].arr.map(_.asInstanceOf[JInt].num.toInt).toSet
          )
        case ("BoxOrderChanged", _) => BoxOrderChanged()
        case ("UnchangedSolution", _) => UnchangedSolution()
      }
    }
  )

  private def simpleSolutionToJObject(solution: SimpleBinPackingSolutionRepresentation): JObject = {
    JObject(
      "placement" -> placementToJObject(solution.placement),
      "update" -> SerializationUtil.toJson(solution.update)
    )
  }

  private def topLeftFirstSolutionToJObject(solution: RectanglePermutationBinPackingSolutionRepresentation): JObject = {
    JObject(
      "placement" -> placementToJObject(solution.placement),
      "update" -> SerializationUtil.toJson(solution.update),
      "permutation" -> SerializationUtil.toJson(solution.permutation)
    )
  }

  private def placementToJObject(placement: Map[Rectangle, Placing]): JArray = JArray(
    placement.toSeq.map {
      case (rectangle, Placing(box, coordinates)) =>
        SerializationUtil.toJson(
          Map(
            "rectangle" -> SerializationUtil.toJson(rectangle),
            "box" -> SerializationUtil.toJson(box),
            "coordinates" -> SerializationUtil.toJson(coordinates)
          )
        )
    }.toList
  )

  private def topLeftCandidatesToJObject(topLeftCandidates: Map[Int, SortedSet[Coordinates]]): JArray = JArray(
    topLeftCandidates.toSeq.map {
      case (boxId, candidates) =>
        SerializationUtil.toJson(
          Map(
            "boxId" -> boxId,
            "candidates" -> SerializationUtil.toJson(candidates)
          )
        )
    }.toList
  )

  private def getFieldValue(jObject: JObject, key: String): JValue = {
    jObject.obj.collect { case (key_, value) if key_ == key => value }.head
  }

  private def jObjectToSimpleSolution(jObject: JObject): SimpleBinPackingSolutionRepresentation = {
    SimpleBinPackingSolutionRepresentation(
      placementFromJObject(jObject),
      SerializationUtil.fromJson[Update](getFieldValue(jObject, "update"))
    )
  }

  private def jObjectToRectanglePermutationSolution(
    jObject: JObject
  ): RectanglePermutationBinPackingSolutionRepresentation = {
    RectanglePermutationBinPackingSolutionRepresentation(
      placementFromJObject(jObject),
      SerializationUtil.fromJson[Update](getFieldValue(jObject, "update")),
      SerializationUtil.fromJson[Seq[Int]](getFieldValue(jObject, "permutation"))
    )
  }

  private def placementFromJObject(jObject: JObject): Map[Rectangle, Placing] = {
    getFieldValue(jObject, "placement")
      .asInstanceOf[JArray]
      .arr
      .map(_.asInstanceOf[JObject])
      .map { placing =>
        val rectangle = SerializationUtil.fromJson[Rectangle](getFieldValue(placing, "rectangle"))
        val box = SerializationUtil.fromJson[Box](getFieldValue(placing, "box"))
        val coordinates = SerializationUtil.fromJson[Coordinates](getFieldValue(placing, "coordinates"))
        rectangle -> Placing(box, coordinates)
      }
      .toMap
  }

}
