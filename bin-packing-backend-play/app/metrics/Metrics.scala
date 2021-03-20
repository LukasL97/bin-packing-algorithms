package metrics

import com.codahale.metrics.Counter
import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.SharedMetricRegistries
import com.codahale.metrics.Timer

trait Metrics {

  def counter(name: String): Counter = MetricRegistryProvider.get.counter(name)

  def timer(name: String): Timer = MetricRegistryProvider.get.timer(name)

}

object MetricRegistryProvider {

  private val name = "my.metrics"
  private val registry = SharedMetricRegistries.getOrCreate(name)

  def get: MetricRegistry = registry
}
