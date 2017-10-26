package de.envisia

import akka.stream.IOResult
import akka.stream.scaladsl.Source
import akka.util.ByteString

import scala.concurrent.Future

sealed abstract class OperationType(val name: String, val operationId: Byte)

case object GetPrinterAttributes                                extends OperationType("Get-Printer-Attributes", 0x0b.toByte)
case class PrintJob(file: Source[ByteString, Future[IOResult]]) extends OperationType("Print-Job", 0x02.toByte)
case object ValidateJob                                         extends OperationType("Validate-Job", 0x04.toByte)
case object GetJobAttributes                                    extends OperationType("Get-Job-Attributes", 0x09.toByte)
