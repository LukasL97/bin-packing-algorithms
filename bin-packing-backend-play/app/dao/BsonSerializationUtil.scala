package dao

import actors.BinPackingSolutionStep
import models.problem.binpacking.solution.Box
import models.problem.binpacking.solution.Coordinates
import models.problem.binpacking.solution.Placing
import models.problem.binpacking.solution.Rectangle
import models.problem.binpacking.solution.SimpleBinPackingSolution
import org.mongodb.scala.bson.BsonArray
import org.mongodb.scala.bson.BsonDocument

object BsonSerializationUtil {

  def solutionStepToDocument(solutionStep: BinPackingSolutionStep): BsonDocument = {
    BsonDocument(
      "runId" -> solutionStep.runId,
      "step" -> solutionStep.step,
      "solution" -> simpleSolutionToDocument(solutionStep.solution),
      "finished" -> solutionStep.finished
    )
  }

  private def simpleSolutionToDocument(solution: SimpleBinPackingSolution): BsonDocument = {
    BsonDocument(
      "jsonClass" -> "SimpleBinPackingSolution",
      "placement" -> placementToDocument(solution.placement)
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
