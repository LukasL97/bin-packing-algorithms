package metrics

import com.codahale.metrics.Counter
import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.SharedMetricRegistries
import com.codahale.metrics.Timer
import org.slf4j.MDC

import scala.jdk.CollectionConverters._

trait Metrics {

  def counter(name: String, context: (String, String)*): Counter = {
    withContext(context: _*) {
      val fullName = buildMetricName(name, getContext)
      MetricRegistryProvider.get.counter(fullName)
    }
  }

  private def timer(name: String, context: (String, String)*): Timer = {
    withContext(context: _*) {
      val fullName = buildMetricName(name, getContext)
      MetricRegistryProvider.get.timer(fullName)
    }
  }

  def withTimer[A](name: String, context: (String, String)*)(f: => A): A = {
    timer(name, context: _*).time {
      () => f
    }
  }

  def withContext[A](context: (String, String)*)(f: => A): A = {
    val previousValues = context.flatMap {
      case (key, _) =>
        Option(MDC.get(key)).map { oldValue =>
          key -> oldValue
        }
    }
    context.foreach {
      case (key, value) => MDC.put(key, Option(value).getOrElse("null"))
    }
    try {
      f
    } finally {
      context.foreach {
        case (key, _) => MDC.remove(key)
      }
      previousValues.foreach {
        case (key, value) => MDC.put(key, value)
      }
    }
  }

  private def getContext: Map[String, String] = {
    Option(MDC.getCopyOfContextMap)
      .map(_.asScala.toMap)
      .getOrElse(Map.empty[String, String])
  }

  private def buildMetricName(name: String, context: Map[String, String]): String = {
    val contextString = context.map {
      case (key, value) => key + ":" + value
    }.mkString("_")
    name + "_" + contextString
  }

}

object MetricRegistryProvider {

  private val name = "my.metrics"
  private val registry = SharedMetricRegistries.getOrCreate(name)

  def get: MetricRegistry = registry
}
