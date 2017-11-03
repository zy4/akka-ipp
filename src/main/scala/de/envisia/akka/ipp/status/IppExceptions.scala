package de.envisia.akka.ipp.status

object IppExceptions {

  case class WrongRequestType(private val message: String = "Wrong Request Type",
      private val cause: Throwable = None.orNull)
      extends Exception(message, cause)

}