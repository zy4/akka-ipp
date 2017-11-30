package de.envisia.akka.ipp

import de.envisia.akka.ipp.attributes.Attributes

import scala.concurrent.duration._

case class IPPConfig(
    host: String,
    port: Int = Attributes.WELL_KNOWN_PORT,
    queue: String = "print",
    username: Option[String] = None,
    pollingInterval: FiniteDuration = 2.seconds,
)
