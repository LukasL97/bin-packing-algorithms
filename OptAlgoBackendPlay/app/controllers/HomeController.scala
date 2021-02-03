package controllers

import dao.RectanglesPlacementSolutionDAO
import models.problem.rectangles.RectanglesPlacementSolution
import play.api.mvc._

import javax.inject._
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents, val dao: RectanglesPlacementSolutionDAO) extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok
  }

  def mongoTest() = Action { implicit request: Request[AnyContent] =>
    dao.dumpSolution("run", 42, RectanglesPlacementSolution(Map()))
    val result = Await.result(dao.getSolutionsBetweenSteps("run", 42, 42), 1 second)
    Ok(result.toString())
  }

}