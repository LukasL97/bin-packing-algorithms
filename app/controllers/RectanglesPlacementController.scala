package controllers

import actors.RectanglesPlacementExecutor
import akka.actor.ActorSystem
import play.api.mvc.{BaseController, ControllerComponents}

import javax.inject.{Inject, Singleton}

@Singleton
class RectanglesPlacementController @Inject()(
  val controllerComponents: ControllerComponents,
  val system: ActorSystem
) extends BaseController {

  private val rectanglesPlacementActor = system.actorOf(
    RectanglesPlacementExecutor.props,
    "rectangles-placement-actor"
  )

}
