package controllers

import dao.BinPackingInstanceDAO
import models.problem.binpacking.BinPackingInstance
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.BaseController
import play.api.mvc.ControllerComponents
import play.api.mvc.Request
import utils.SerializationUtil

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext
import utils.JsonConversions._

import java.util.Date

@Singleton
class InstanceController @Inject()(
  val controllerComponents: ControllerComponents,
  val instanceDao: BinPackingInstanceDAO,
  implicit val ec: ExecutionContext
) extends BaseController {

  def getAllInstances: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    instanceDao.getAllInstances
      .map(instances => instances.map(InstanceResponse.apply))
      .map(instanceResponses => SerializationUtil.toJson(instanceResponses))
      .map(toPlayJson)
      .map(Ok(_))
  }

}

case class InstanceResponse(
  id: String,
  creationDate: Date,
  boxLength: Int,
  numRectangles: Int,
  minWidth: Int,
  maxWidth: Int,
  minHeight: Int,
  maxHeight: Int
)

object InstanceResponse {
  def apply(instance: BinPackingInstance): InstanceResponse = new InstanceResponse(
    instance.id,
    instance.creationDate,
    instance.boxLength,
    instance.numRectangles,
    instance.minWidth,
    instance.maxWidth,
    instance.minHeight,
    instance.maxHeight
  )
}
