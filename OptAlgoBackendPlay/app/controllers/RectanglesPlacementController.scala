package controllers

import actors.RectanglesPlacementActor
import actors.RectanglesPlacementSolutionStep
import akka.actor.ActorRef
import akka.actor.ActorRef.noSender
import akka.actor.ActorSystem
import akka.actor.Props
import controllers.exceptions.UnknownStrategyException
import dao.RectanglesPlacementSolutionStepDAO
import models.problem.rectangles.RectanglesPlacement
import models.problem.rectangles.greedy.RandomSelectionRectanglesPlacementGreedy
import models.problem.rectangles.localsearch.GeometryBasedRectanglesPlacement
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
  val rectanglesPlacementActorStarter: RectanglesPlacementActorStarter,
  val dao: RectanglesPlacementSolutionStepDAO,
  implicit val ec: ExecutionContext
) extends BaseController {

  def start(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    try {
      request.body.asJson.map { json =>
        val startInfo = SerializationUtil.fromJson[StartRequestBody](json)
        val startSolution = rectanglesPlacementActorStarter(startInfo)
        val response: JsValue = SerializationUtil.toJson(startSolution)
        Ok(response)
      }.getOrElse(
        BadRequest
      )
    } catch {
      case e: UnknownStrategyException => BadRequest(e.getMessage)
    }
  }

  def getSteps(runId: String, minStep: String, maxStep: String): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      dao
        .getSolutionStepsInStepRange(runId, parseInt(minStep), parseInt(maxStep))
        .map(solutionSteps => SerializationUtil.toJson(solutionSteps))
        .map(response => Ok(toPlayJson(response)))
  }

}

@Singleton
class RectanglesPlacementActorStarter @Inject()(
  val system: ActorSystem,
  val rectanglesPlacementActorFactory: RectanglesPlacementActor.Factory
) {

  def apply(startInfo: StartRequestBody): RectanglesPlacementSolutionStep = {
    val runId = generateRunId()
    val actor = createActor(runId)
    val rectanglesPlacement = RectanglesPlacementProvider.get(
      startInfo.strategy,
      startInfo.boxLength,
      startInfo.numRectangles,
      (startInfo.rectanglesWidthRange.min, startInfo.rectanglesWidthRange.max),
      (startInfo.rectanglesHeightRange.min, startInfo.rectanglesHeightRange.max)
    )
    val startSolution = rectanglesPlacement.startSolution
    actor.tell((runId, rectanglesPlacement), noSender)
    RectanglesPlacementSolutionStep.getStartStep(runId, startSolution)
  }

  private def createActor(runId: String): ActorRef = system.actorOf(
    Props(rectanglesPlacementActorFactory.apply()),
    s"rectangles-placement-actor-$runId"
  )

  private def generateRunId(): String = UUID.randomUUID().toString
}

object RectanglesPlacementProvider {
  def get(
    strategy: String,
    boxLength: Int,
    numRectangles: Int,
    rectangleWidthRange: (Int, Int),
    rectangleHeightRange: (Int, Int)
  ): RectanglesPlacement = strategy match {
    case "localSearch geometryBased" =>
      new GeometryBasedRectanglesPlacement(
        boxLength,
        numRectangles,
        rectangleWidthRange,
        rectangleHeightRange
      )
    case "greedy randomSelection" =>
      new RandomSelectionRectanglesPlacementGreedy(
        boxLength,
        numRectangles,
        rectangleWidthRange,
        rectangleHeightRange
      )
    case unknownStrategy => throw UnknownStrategyException(unknownStrategy)
  }
}

case class StartRequestBody(
  strategy: String,
  boxLength: Int,
  numRectangles: Int,
  rectanglesWidthRange: Range,
  rectanglesHeightRange: Range
)

case class Range(
  min: Int,
  max: Int
)
