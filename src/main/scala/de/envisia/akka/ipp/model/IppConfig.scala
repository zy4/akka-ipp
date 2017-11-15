package de.envisia.akka.ipp.model

import akka.http.scaladsl.HttpExt

case class IppConfig(
    host: String = "127.0.0.1",
    port: Int = 631,
    queue: String = "print",
    username: Option[String] = None
)
