package controllers

import com.codahale.metrics.Counter
import com.codahale.metrics.Metric
import com.codahale.metrics.Timer
import metrics.MetricRegistryProvider
import play.api.libs.json.JsValue
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.BaseController
import play.api.mvc.ControllerComponents
import play.api.mvc.Request
import utils.JsonConversions._
import utils.SerializationUtil

import javax.inject.Inject
import javax.inject.Singleton
import scala.jdk.CollectionConverters._

@Singleton
class MetricsController @Inject()(
  val controllerComponents: ControllerComponents
) extends BaseController {

  def get(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val metrics: Map[String, Metric] = MetricRegistryProvider.get.getMetrics.asScala.toMap
    val metricsResults = metrics.collect {
      case (name, counter: Counter) => name -> CounterResult(counter.getCount)
      case (name, timer: Timer) =>
        name -> TimerResult(
          timer.getSnapshot.getMean,
          timer.getSnapshot.getStdDev,
          timer.getSnapshot.getMedian,
          timer.getSnapshot.getMin,
          timer.getSnapshot.getMax
        )
    }
    val results: JsValue = SerializationUtil.toJson(metricsResults)
    Ok(results)
  }

}

sealed trait MetricResult

case class CounterResult(
  count: Long)
  extends MetricResult

case class TimerResult(
  mean: Double,
  stddev: Double,
  median: Double,
  min: Long,
  max: Long
) extends MetricResult
