package dao

import actors.BinPackingSolutionStep
import models.problem.binpacking.solution.BinPackingSolutionRepresentation
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
import org.mongodb.scala.bson.BsonArray
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.bson.BsonInt32

object BsonSerializationUtil {

  def solutionStepToDocument(solutionStep: BinPackingSolutionStep): BsonDocument = {
    BsonDocument(
      "runId" -> solutionStep.runId,
      "step" -> solutionStep.step,
      "solution" -> solutionToDocument(solutionStep.solution),
      "finished" -> solutionStep.finished
    )
  }

  private def solutionToDocument(solution: BinPackingSolutionRepresentation): BsonDocument = solution match {
    case solution: SimpleBinPackingSolutionRepresentation => simpleSolutionToDocument(solution)
    case solution: RectanglePermutationBinPackingSolutionRepresentation => rectanglePermutationSolutionToDocument(solution)
  }

  private def updateToDocument(update: Update): BsonDocument = update match {
    case StartSolution() => BsonDocument("jsonClass" -> "StartSolution")
    case RectanglesChanged(rectangleIds) =>
      BsonDocument(
        "jsonClass" -> "RectanglesChanged",
        "rectangleIds" -> BsonArray.fromIterable(rectangleIds.map(BsonInt32(_)))
      )
    case BoxOrderChanged() => BsonDocument("jsonClass" -> "BoxOrderChanged")
    case UnchangedSolution() => BsonDocument("jsonClass" -> "UnchangedSolution")
  }

  private def simpleSolutionToDocument(solution: SimpleBinPackingSolutionRepresentation): BsonDocument = {
    BsonDocument(
      "jsonClass" -> "SimpleBinPackingSolutionRepresentation",
      "placement" -> placementToDocument(solution.placement),
      "update" -> updateToDocument(solution.update)
    )
  }

  private def rectanglePermutationSolutionToDocument(solution: RectanglePermutationBinPackingSolutionRepresentation): BsonDocument = {
    BsonDocument(
      "jsonClass" -> "RectanglePermutationBinPackingSolutionRepresentation",
      "placement" -> placementToDocument(solution.placement),
      "update" -> updateToDocument(solution.update),
      "permutation" -> BsonArray.fromIterable(solution.permutation.map(BsonInt32(_)))
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
