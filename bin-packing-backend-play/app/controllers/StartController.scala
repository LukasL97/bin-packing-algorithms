package controllers

import actors.BinPackingActor
import actors.BinPackingSolutionStep
import akka.actor.ActorRef
import akka.actor.ActorRef.noSender
import akka.actor.ActorSystem
import akka.actor.Props
import controllers.exceptions.UnknownStrategyException
import dao.BinPackingInstanceDAO
import dao.BinPackingSolutionStepDAO
import models.problem.binpacking.BinPacking
import models.problem.binpacking.BinPackingInstance
import models.problem.binpacking.greedy.BoxClosingBinPackingGreedy
import models.problem.binpacking.greedy.basic.RandomSelectionBinPackingGreedy
import models.problem.binpacking.greedy.basic.SizeOrderedBinPackingGreedy
import models.problem.binpacking.greedy.candidatesupported.{RandomSelectionBinPackingGreedy => QuickRandomSelectionBinPackingGreedy}
import models.problem.binpacking.greedy.candidatesupported.{SizeOrderedBinPackingGreedy => QuickSizeOrderedBinPackingGreedy}
import models.problem.binpacking.localsearch.EventuallyFeasibleGeometryBasedBinPacking
import models.problem.binpacking.localsearch.GeometryBasedBinPacking
import models.problem.binpacking.localsearch.RectanglePermutationBinPacking
import models.problem.binpacking.localsearch.TopLeftFirstBoxMergingBinPacking
import models.problem.binpacking.localsearch.TopLeftFirstOverlappingBinPacking
import play.api.libs.json.JsValue
import play.api.mvc.Action
import play.api.mvc._
import utils.BinPackingSolutionSerializationUtil.formats
import utils.JsonConversions._
import utils.SerializationUtil

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

@Singleton
class StartController @Inject()(
  val controllerComponents: ControllerComponents,
  val actorStarter: BinPackingActorStarter,
  val solutionStepDao: BinPackingSolutionStepDAO,
  val instanceDao: BinPackingInstanceDAO,
  implicit val ec: ExecutionContext
) extends BaseController {

  def start(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    try {
      request.body.asJson.map { json =>
        val startInfo = SerializationUtil.fromJson[StartRequestBody](json)
        val instance = BinPackingInstance(
          startInfo.boxLength,
          startInfo.numRectangles,
          startInfo.rectanglesWidthRange.min,
          startInfo.rectanglesWidthRange.max,
          startInfo.rectanglesHeightRange.min,
          startInfo.rectanglesHeightRange.max
        )
        instanceDao.dumpInstance(instance)
        val startSolution = actorStarter(startInfo.strategy, instance, startInfo.timeLimit)
        val response: JsValue = SerializationUtil.toJson(startSolution)
        Ok(response)
      }.getOrElse(
        BadRequest
      )
    } catch {
      case e: UnknownStrategyException => BadRequest(e.getMessage)
    }
  }

  def startFromInstance(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    request.body.asJson.map { json =>
      val startInfo = SerializationUtil.fromJson[StartFromInstanceRequestBody](json)
      instanceDao.getInstance(startInfo.instanceId).map { instance =>
        try {
          val startSolution = actorStarter(startInfo.strategy, instance, startInfo.timeLimit)
          val response: JsValue = SerializationUtil.toJson(startSolution)
          Ok(response)
        } catch {
          case e: UnknownStrategyException => BadRequest(e.getMessage)
        }
      }
    }.getOrElse(Future.successful(BadRequest))
  }

}

@Singleton
class BinPackingActorStarter @Inject()(
  val system: ActorSystem,
  val actorFactory: BinPackingActor.Factory
) {

  def apply(strategy: String, instance: BinPackingInstance, timeLimit: Option[Int]): BinPackingSolutionStep = {
    val runId = generateRunId()
    val actor = createActor(runId)
    val binPacking = BinPackingProvider.get(strategy, instance)
    val startSolution = binPacking.startSolution.asSimpleSolution
    actor.tell((runId, binPacking, timeLimit), noSender)
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
    instance: BinPackingInstance
  ): BinPacking = strategy match {
    case "localSearch geometryBased" => new GeometryBasedBinPacking(instance)
    case "localSearch eventuallyFeasibleGeometryBased" => new EventuallyFeasibleGeometryBasedBinPacking(instance)
    case "localSearch boxMerging" => new TopLeftFirstBoxMergingBinPacking(instance)
    case "localSearch overlapping" => new TopLeftFirstOverlappingBinPacking(instance)
    case "localSearch rectanglePermutation" => new RectanglePermutationBinPacking(instance)
    case "greedy randomSelection" => new RandomSelectionBinPackingGreedy(instance)
    case "greedy sizeOrdered" => new SizeOrderedBinPackingGreedy(instance)
    case "greedy2 randomSelection" => new QuickRandomSelectionBinPackingGreedy(instance)
    case "greedy2 sizeOrdered" => new QuickSizeOrderedBinPackingGreedy(instance)
    case "greedy boxClosing" => new BoxClosingBinPackingGreedy(instance)
    case unknownStrategy => throw UnknownStrategyException(unknownStrategy)
  }
}

case class StartRequestBody(
  strategy: String,
  boxLength: Int,
  numRectangles: Int,
  rectanglesWidthRange: Range,
  rectanglesHeightRange: Range,
  timeLimit: Option[Int]
)

case class Range(
  min: Int,
  max: Int
)

case class StartFromInstanceRequestBody(
  strategy: String,
  instanceId: String,
  timeLimit: Option[Int]
)
