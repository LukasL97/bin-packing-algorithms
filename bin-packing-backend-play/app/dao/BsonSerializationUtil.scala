package dao

import actors.BinPackingSolutionStep
import models.problem.binpacking.solution.BinPackingSolution
import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.SimpleBinPackingSolution
import models.problem.binpacking.solution.TopLeftFirstBinPackingSolution
import org.mongodb.scala.bson.BsonArray
import org.mongodb.scala.bson.BsonDocument

import scala.collection.SortedSet

object BsonSerializationUtil {

  def solutionStepToDocument(solutionStep: BinPackingSolutionStep): BsonDocument = {
    BsonDocument(
      "runId" -> solutionStep.runId,
      "step" -> solutionStep.step,
      "solution" -> solutionToDocument(solutionStep.solution),
      "finished" -> solutionStep.finished
    )
  }

  private def solutionToDocument(solution: BinPackingSolution): BsonDocument = solution match {
    case solution: SimpleBinPackingSolution => simpleSolutionToDocument(solution)
    case solution: TopLeftFirstBinPackingSolution => topLeftFirstSolutionToDocument(solution)
  }

  private def simpleSolutionToDocument(solution: SimpleBinPackingSolution): BsonDocument = {
    BsonDocument(
      "jsonClass" -> "SimpleBinPackingSolution",
      "placement" -> placementToDocument(solution.placement)
    )
  }

  private def topLeftFirstSolutionToDocument(solution: TopLeftFirstBinPackingSolution): BsonDocument = {
    BsonDocument(
      "jsonClass" -> "TopLeftFirstBinPackingSolution",
      "placement" -> placementToDocument(solution.placement),
      "topLeftCandidates" -> topLeftCandidatesToDocument(solution.topLeftCandidates),
      "boxLength" -> solution.boxLength
    )
  }

  private def placementToDocument(placement: Map[Rectangle, Placing]): BsonArray = {
    BsonArray.fromIterable(
      placement.toSeq.map {
        case (rectangle, Placing(box, coordinates)) =>
          BsonDocument(
            "rectangle" -> rectangleToDocument(rectangle),
            "box" -> boxToDocument(box),
            "coordinates" -> coordinatesToDocument(coordinates)
          )
      }
    )
  }

  def topLeftCandidatesToDocument(topLeftCandidates: Map[Int, SortedSet[Coordinates]]): BsonArray = {
    BsonArray.fromIterable(
      topLeftCandidates.toSeq.map {
        case (boxId, candidates) =>
          BsonDocument(
            "boxId" -> boxId,
            "candidates" -> BsonArray.fromIterable(
              candidates.toSeq.map(coordinatesToDocument)
            )
          )
      }
    )
  }

  private def rectangleToDocument(rectangle: Rectangle): BsonDocument = BsonDocument(
    "id" -> rectangle.id,
    "width" -> rectangle.width,
    "height" -> rectangle.height
  )

  private def boxToDocument(box: Box): BsonDocument = BsonDocument(
    "id" -> box.id,
    "length" -> box.length
  )

  private def coordinatesToDocument(coordinates: Coordinates): BsonDocument = BsonDocument(
    "x" -> coordinates.x,
    "y" -> coordinates.y
  )

}
