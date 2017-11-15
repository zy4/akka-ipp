package de.envisia.akka.ipp.model

import scala.concurrent.duration._

case class IppConfig(
    host: String = "127.0.0.1",
    port: Int = 631,
    queue: String = "print",
    username: Option[String] = None,
    pollingInterval: FiniteDuration = 2.seconds,
)
