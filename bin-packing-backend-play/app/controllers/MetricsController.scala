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
    request.body.asJson.map { json =>
      val query = SerializationUtil.fromJson[MetricQueryBody](json)
      val metrics: Map[String, Metric] = MetricRegistryProvider.get.getMetrics.asScala.toMap
      val filteredMetrics = metrics.map {
        case (name, result) =>
          decomposeMetricName(name) -> result
      }.toSeq.collect {
        case ((name, tags), metric) if matchesQuery(name, tags, query) =>
          MetricQueryResponseElement(
            name,
            tags.map { case (key, value) => MetricTag(key, value) }.toSeq,
            metric.getClass.getSimpleName,
            toMetricResult(metric)
          )
      }
      val results: JsValue = SerializationUtil.toJson(filteredMetrics)
      Ok(results)
    }.getOrElse(
      BadRequest
    )
  }

  private def toMetricResult(metric: Metric): MetricResult = metric match {
    case counter: Counter => CounterResult(counter.getCount)
    case timer: Timer =>
      TimerResult(
        timer.getCount,
        timer.getSnapshot.getMean,
        timer.getSnapshot.getStdDev,
        timer.getSnapshot.getMedian,
        timer.getSnapshot.getMin,
        timer.getSnapshot.getMax
      )
    case other => throw new RuntimeException(s"Unexpected metric type ${other.getClass.getSimpleName}")
  }

  private def decomposeMetricName(name: String): (String, Map[String, String]) = {
    val components = name.split("_").toSeq
    val actualName = components.head
    val tags = components.tail
      .map(
        tagString =>
          tagString.split(":").toSeq match {
            case Seq(key, value) => key -> value
            case _ => throw new RuntimeException(s"Tag string was wrongly formatted: '$tagString'")
        }
      )
      .toMap
    (actualName, tags)
  }

  private def matchesQuery(name: String, tags: Map[String, String], query: MetricQueryBody): Boolean = {
    query.name.forall(_ == name) && tags.map {
      case (key, value) =>
        query.filters.forall { filters =>
          filters.filter(_.key == key).forall(_.value == value)
        }
    }.forall(identity)
  }

}

case class MetricQueryBody(
  name: Option[String],
  filters: Option[Seq[MetricQueryFilter]]
)

case class MetricQueryFilter(
  key: String,
  value: String
)

case class MetricQueryResponseElement(
  name: String,
  tags: Seq[MetricTag],
  metricType: String,
  result: MetricResult
)

case class MetricTag(
  key: String,
  value: String
)

sealed trait MetricResult

case class CounterResult(count: Long) extends MetricResult

case class TimerResult(
  count: Long,
  mean: Double,
  stddev: Double,
  median: Double,
  min: Long,
  max: Long
) extends MetricResult
