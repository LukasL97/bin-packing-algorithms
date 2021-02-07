package controllers

import actors.RectanglesPlacementActor
import actors.RectanglesPlacementSolutionStep
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorRef.noSender
import akka.actor.ActorSystem
import akka.actor.Props
import dao.RectanglesPlacementSolutionStepDAO
import models.problem.rectangles.GeometryBasedRectanglesPlacement
import models.problem.rectangles.RectanglesPlacement
import play.api.libs.json.JsValue
import play.api.mvc._
import utils.JsonConversions._
import utils.RectanglesPlacementSolutionSerializationUtil.formats
import utils.SerializationUtil

import java.lang.Integer.parseInt
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext


@Singleton
class RectanglesPlacementController @Inject()(
  val controllerComponents: ControllerComponents,
  val system: ActorSystem,
  val rectanglesPlacementActorFactory: RectanglesPlacementActor.Factory,
  val dao: RectanglesPlacementSolutionStepDAO,
  implicit val ec: ExecutionContext
) extends BaseController {


  def injectedChild2(create: => Actor, name: String): ActorRef = {
    system.actorOf(Props(create), name)
  }

  private def createExecutor(runId: String): ActorRef = system.actorOf(
    Props(rectanglesPlacementActorFactory.apply()),
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
    RectanglesPlacementSolutionStep.getStartStep(runId, startSolution)
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

  def getSteps(runId: String, minStep: String, maxStep: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    dao.getSolutionStepsInStepRange(runId, parseInt(minStep), parseInt(maxStep))
      .map(solutionSteps => SerializationUtil.toJson(solutionSteps))
      .map(response => Ok(toPlayJson(response)))
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


