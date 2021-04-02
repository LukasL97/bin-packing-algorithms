package utils

import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.SimpleBinPackingSolution
import models.problem.binpacking.solution.TopLeftFirstBinPackingSolution
import models.problem.binpacking.solution.update.RectanglesChanged
import models.problem.binpacking.solution.update.StartSolution
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

object BinPackingSolutionSerializationUtil extends TopLeftFirstCoordinateOrdering {

  implicit val formats: Formats = SerializationUtil.defaultFormats(
    new ShortTypeHints(
      List(
        classOf[SimpleBinPackingSolution],
        classOf[TopLeftFirstBinPackingSolution],
        classOf[StartSolution],
        classOf[RectanglesChanged]
      )
    ) {

      override def serialize: PartialFunction[Any, json4s.JObject] = {
        case solution: SimpleBinPackingSolution => simpleSolutionToJObject(solution)
        case solution: TopLeftFirstBinPackingSolution => topLeftFirstSolutionToJObject(solution)
        case update: StartSolution => JObject()
        case update: RectanglesChanged => JObject("rectangleIds" -> SerializationUtil.toJson(update.rectangleIds))
      }

      override def deserialize: PartialFunction[(String, json4s.JObject), Any] = {
        case ("SimpleBinPackingSolution", jObject) => jObjectToSimpleSolution(jObject)
        case ("TopLeftFirstBinPackingSolution", jObject) => jObjectToTopLeftFirstSolution(jObject)
        case ("StartSolution", jObject) => StartSolution()
        case ("RectanglesChanged", jObject) =>
          RectanglesChanged(
            getFieldValue(jObject, "rectangleIds").asInstanceOf[JArray].arr.map(_.asInstanceOf[JInt].num.toInt).toSet
          )
      }
    }
  )

  private def simpleSolutionToJObject(solution: BinPackingSolution): JObject = {
    JObject(
      "placement" -> placementToJObject(solution.placement),
      "update" -> SerializationUtil.toJson(solution.update)
    )
  }

  private def topLeftFirstSolutionToJObject(solution: TopLeftFirstBinPackingSolution): JObject = JObject(
    "placement" -> placementToJObject(solution.placement),
    "topLeftCandidates" -> topLeftCandidatesToJObject(solution.topLeftCandidates),
    "boxLength" -> JInt(solution.boxLength),
    "update" -> SerializationUtil.toJson(solution.update)
  )

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

  private def jObjectToSimpleSolution(jObject: JObject): SimpleBinPackingSolution = new SimpleBinPackingSolution(
    placementFromJObject(jObject),
    SerializationUtil.fromJson[Update](getFieldValue(jObject, "update"))
  )

  private def jObjectToTopLeftFirstSolution(jObject: JObject): TopLeftFirstBinPackingSolution = {
    new TopLeftFirstBinPackingSolution(
      placementFromJObject(jObject),
      topLeftCandidatesFromJObject(jObject),
      getFieldValue(jObject, "boxLength").asInstanceOf[JInt].num.toInt,
      SerializationUtil.fromJson[Update](getFieldValue(jObject, "update"))
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

  private def topLeftCandidatesFromJObject(jObject: JObject): Map[Int, SortedSet[Coordinates]] = {
    getFieldValue(jObject, "topLeftCandidates")
      .asInstanceOf[JArray]
      .arr
      .map(_.asInstanceOf[JObject])
      .map { boxCandidates =>
        val boxId = getFieldValue(boxCandidates, "boxId").asInstanceOf[JInt].num.toInt
        val candidates = getFieldValue(boxCandidates, "candidates")
          .asInstanceOf[JArray]
          .arr
          .map(_.asInstanceOf[JObject])
          .map { candidate =>
            SerializationUtil.fromJson[Coordinates](candidate)
          }
        boxId -> SortedSet.from(candidates)
      }
      .toMap
  }

}
