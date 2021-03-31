package controllers

import actors.BinPackingActor
import actors.BinPackingSolutionStep
import akka.actor.ActorRef
import akka.actor.ActorRef.noSender
import akka.actor.ActorSystem
import akka.actor.Props
import controllers.exceptions.UnknownStrategyException
import dao.BinPackingSolutionStepDAO
import models.problem.binpacking.BinPacking
import models.problem.binpacking.greedy.BoxClosingBinPackingGreedy
import models.problem.binpacking.greedy.basic.RandomSelectionBinPackingGreedy
import models.problem.binpacking.greedy.basic.SizeOrderedBinPackingGreedy
import models.problem.binpacking.greedy.candidatesupported.{
  RandomSelectionBinPackingGreedy => QuickRandomSelectionBinPackingGreedy
}
import models.problem.binpacking.greedy.candidatesupported.{
  SizeOrderedBinPackingGreedy => QuickSizeOrderedBinPackingGreedy
}
import models.problem.binpacking.localsearch.EventuallyFeasibleGeometryBasedBinPacking
import models.problem.binpacking.localsearch.GeometryBasedBinPacking
import models.problem.binpacking.localsearch.RectanglePermutationBinPacking
import models.problem.binpacking.localsearch.TopLeftFirstBoxMergingBinPacking
import models.problem.binpacking.localsearch.TopLeftFirstOverlappingBinPacking
import play.api.libs.json.JsValue
import play.api.mvc._
import utils.BinPackingSolutionSerializationUtil.formats
import utils.JsonConversions._
import utils.SerializationUtil

import java.lang.Integer.parseInt
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext

@Singleton
class BinPackingController @Inject()(
  val controllerComponents: ControllerComponents,
  val actorStarter: BinPackingActorStarter,
  val dao: BinPackingSolutionStepDAO,
  implicit val ec: ExecutionContext
) extends BaseController {

  def start(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    try {
      request.body.asJson.map { json =>
        val startInfo = SerializationUtil.fromJson[StartRequestBody](json)
        val startSolution = actorStarter(startInfo)
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

  def getRawSteps(runId: String, minStep: String, maxStep: String): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      dao
        .getRawSolutionsStepsInStepRange(runId, parseInt(minStep), parseInt(maxStep))
        .map(toPlayJson)
        .map(Ok(_))
  }

}

@Singleton
class BinPackingActorStarter @Inject()(
  val system: ActorSystem,
  val actorFactory: BinPackingActor.Factory
) {

  def apply(startInfo: StartRequestBody): BinPackingSolutionStep = {
    val runId = generateRunId()
    val actor = createActor(runId)
    val binPacking = BinPackingProvider.get(
      startInfo.strategy,
      startInfo.boxLength,
      startInfo.numRectangles,
      (startInfo.rectanglesWidthRange.min, startInfo.rectanglesWidthRange.max),
      (startInfo.rectanglesHeightRange.min, startInfo.rectanglesHeightRange.max)
    )
    val startSolution = binPacking.startSolution.asSimpleSolution
    actor.tell((runId, binPacking), noSender)
    BinPackingSolutionStep.startStep(runId, startSolution)
  }

  private def createActor(runId: String): ActorRef = system.actorOf(
    Props(actorFactory.apply()),
    s"bin-packing-actor-$runId"
  )

  private def generateRunId(): String = UUID.randomUUID().toString
}

object BinPackingProvider {
  def get(
    strategy: String,
    boxLength: Int,
    numRectangles: Int,
    rectangleWidthRange: (Int, Int),
    rectangleHeightRange: (Int, Int)
  ): BinPacking = strategy match {
    case "localSearch geometryBased" =>
      new GeometryBasedBinPacking(
        boxLength,
        numRectangles,
        rectangleWidthRange,
        rectangleHeightRange
      )
    case "localSearch eventuallyFeasibleGeometryBased" =>
      new EventuallyFeasibleGeometryBasedBinPacking(
        boxLength,
        numRectangles,
        rectangleWidthRange,
        rectangleHeightRange
      )
    case "localSearch boxMerging" =>
      new TopLeftFirstBoxMergingBinPacking(
        boxLength,
        numRectangles,
        rectangleWidthRange,
        rectangleHeightRange
      )
    case "localSearch overlapping" =>
      new TopLeftFirstOverlappingBinPacking(
        boxLength,
        numRectangles,
        rectangleWidthRange,
        rectangleHeightRange
      )
    case "localSearch rectanglePermutation" =>
      new RectanglePermutationBinPacking(
        boxLength,
        numRectangles,
        rectangleWidthRange,
        rectangleHeightRange
      )
    case "greedy randomSelection" =>
      new RandomSelectionBinPackingGreedy(
        boxLength,
        numRectangles,
        rectangleWidthRange,
        rectangleHeightRange
      )
    case "greedy sizeOrdered" =>
      new SizeOrderedBinPackingGreedy(
        boxLength,
        numRectangles,
        rectangleWidthRange,
        rectangleHeightRange
      )
    case "greedy2 randomSelection" =>
      new QuickRandomSelectionBinPackingGreedy(
        boxLength,
        numRectangles,
        rectangleWidthRange,
        rectangleHeightRange
      )
    case "greedy2 sizeOrdered" =>
      new QuickSizeOrderedBinPackingGreedy(
        boxLength,
        numRectangles,
        rectangleWidthRange,
        rectangleHeightRange
      )
    case "greedy boxClosing" =>
      new BoxClosingBinPackingGreedy(
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
