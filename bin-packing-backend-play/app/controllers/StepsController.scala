package controllers

import dao.AbstractBinPackingSolutionStepDAO
import dao.BinPackingSolutionStepDAO
import dao.CombinedBinPackingSolutionStepDAO
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.BaseController
import play.api.mvc.ControllerComponents
import play.api.mvc.Request
import utils.JsonConversions.toPlayJson
import utils.SerializationUtil
import utils.BinPackingSolutionRepresentationSerializationUtil.formats

import java.lang.Integer.parseInt
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class StepsController @Inject()(
  val controllerComponents: ControllerComponents,
  val solutionStepDao: BinPackingSolutionStepDAO,
  val combinedSolutionStepDao: CombinedBinPackingSolutionStepDAO,
  implicit val ec: ExecutionContext
) extends BaseController {

  def getSteps(runId: String, minStep: String, maxStep: String, combined: Option[Boolean]): Action[AnyContent] = {
    Action.async { implicit request: Request[AnyContent] =>
      getDao(combined)
        .getSolutionStepsInStepRange(runId, parseInt(minStep), parseInt(maxStep))
        .map(solutionSteps => SerializationUtil.toJson(solutionSteps))
        .map(response => Ok(toPlayJson(response)))
    }
  }

  def getRawSteps(runId: String, minStep: String, maxStep: String, combined: Option[Boolean]): Action[AnyContent] = {
    Action.async { implicit request: Request[AnyContent] =>
      getDao(combined)
        .getRawSolutionsStepsInStepRange(runId, parseInt(minStep), parseInt(maxStep))
        .map(toPlayJson)
        .map(Ok(_))
    }
  }

  private def getDao(combined: Option[Boolean]): AbstractBinPackingSolutionStepDAO = {
    combined.collect {
      case true => combinedSolutionStepDao
    }.getOrElse(solutionStepDao)
  }

}
