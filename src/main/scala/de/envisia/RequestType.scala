package de.envisia

import akka.stream.IOResult
import akka.stream.scaladsl.Source
import akka.util.ByteString

import scala.concurrent.Future

sealed abstract class RequestType(val name: String, val operationId: Byte)

case object GetPrinterAttributes                                extends RequestType("Get-Printer-Attributes", 0x0b.toByte)
case class PrintJob(file: Source[ByteString, Future[IOResult]]) extends RequestType("Print-Job", 0x02.toByte)
case object ValidateJob                                         extends RequestType("Validate-Job", 0x04.toByte)
case object GetJobAttributes                                    extends RequestType("Get-Job-Attributes", 0x09.toByte)
