package controllers

import actors.RectanglesPlacementExecutor
import actors.RectanglesPlacementSolutionStep
import akka.actor.ActorRef
import akka.actor.ActorRef.noSender
import akka.actor.ActorSystem
import models.problem.rectangles.GeometryBasedRectanglesPlacement
import models.problem.rectangles.RectanglesPlacement
import play.api.libs.json.JsValue
import play.api.mvc._
import utils.JsonConversions._
import utils.RectanglesPlacementSolutionSerializationUtil.formats
import utils.SerializationUtil

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RectanglesPlacementController @Inject()(
  val controllerComponents: ControllerComponents,
  val system: ActorSystem
) extends BaseController {

  private def createExecutor(runId: String): ActorRef = system.actorOf(
    RectanglesPlacementExecutor.props,
    s"rectangles-placement-executor-$runId"
  )

  private def generateRunId(): String = UUID.randomUUID().toString

  private def startExecutor(startInfo: StartRequestBody): RectanglesPlacementSolutionStep = {
    val runId = generateRunId()
    val executor = createExecutor(runId)
    val rectanglesPlacement = RectanglesPlacementProvider.get(
      startInfo.strategy,
      startInfo.boxLength,
      startInfo.numRectangles,
      (startInfo.rectanglesWidthRange.min, startInfo.rectanglesWidthRange.max),
      (startInfo.rectanglesHeightRange.min, startInfo.rectanglesHeightRange.max)
    )
    val startSolution = rectanglesPlacement.localSearch.startSolution
    executor.tell((runId, rectanglesPlacement), noSender)
    RectanglesPlacementSolutionStep(
      runId,
      0,
      startSolution
    )
  }

  def start(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    request.body.asJson.map { json =>
      val startInfo = SerializationUtil.fromJson[StartRequestBody](json)
      val startSolution = startExecutor(startInfo)
      val response: JsValue = SerializationUtil.toJson(startSolution)
      Ok(response)
    }.getOrElse(
      BadRequest
    )
  }

}

private object RectanglesPlacementProvider {
  def get(
    strategy: String,
    boxLength: Int,
    numRectangles: Int,
    rectangleWidthRange: (Int, Int),
    rectangleHeightRange: (Int, Int)
  ): RectanglesPlacement = strategy match {
    case "geometryBased" => new GeometryBasedRectanglesPlacement(
      boxLength,
      numRectangles,
      rectangleWidthRange,
      rectangleHeightRange
    )
  }
}

private case class StartRequestBody(
  strategy: String,
  boxLength: Int,
  numRectangles: Int,
  rectanglesWidthRange: Range,
  rectanglesHeightRange: Range
)

private case class Range(
  min: Int,
  max: Int
)


