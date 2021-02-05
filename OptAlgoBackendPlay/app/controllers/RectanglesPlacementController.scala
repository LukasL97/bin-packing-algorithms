package controllers

import actors.RectanglesPlacementExecutor
import akka.actor.ActorSystem
import models.problem.rectangles.{GeometryBasedRectanglesPlacement, RectanglesPlacement}
import play.api.libs.json.JsValue
import play.api.mvc._
import utils.JsonConversions._
import utils.SerializationUtil
import utils.RectanglesPlacementSolutionSerializationUtil.formats

import javax.inject.{Inject, Singleton}


@Singleton
class RectanglesPlacementController @Inject()(
  val controllerComponents: ControllerComponents,
  val system: ActorSystem
) extends BaseController {

  private val rectanglesPlacementExecutor = system.actorOf(
    RectanglesPlacementExecutor.props,
    "rectangles-placement-actor"
  )

  def start(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    request.body.asJson.map { json =>
      val startInfo = SerializationUtil.fromJson[StartRequestBody](json)
      val rectanglesPlacement = RectanglesPlacementProvider.get(
        startInfo.strategy,
        startInfo.boxLength,
        startInfo.numRectangles,
        (startInfo.rectanglesWidthRange.min, startInfo.rectanglesWidthRange.max),
        (startInfo.rectanglesHeightRange.min, startInfo.rectanglesHeightRange.max)
      )
      val startSolution = rectanglesPlacement.localSearch.startSolution
      val response: JsValue = SerializationUtil.toJson(startSolution)
      // TODO: Start running algorithm
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


