package controllers.exceptions

case class UnknownStrategyException(strategy: String)
    extends RuntimeException(
      s"Got unknown strategy: '$strategy'"
    )
