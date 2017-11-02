package de.envisia

import akka.stream.IOResult
import akka.stream.scaladsl.Source
import akka.util.ByteString

import scala.concurrent.Future
import de.envisia.attributes.Attributes._

sealed abstract class OperationType(val name: String, val operationId: Byte)

case object GetPrinterAttributes
    extends OperationType("Get-Printer-Attributes", OPERATION_IDS("Get-Printer-Attributes"))

case class PrintJob(file: Source[ByteString, Future[IOResult]])
    extends OperationType("Print-Job", OPERATION_IDS("Print-Job"))

case object ValidateJob extends OperationType("Validate-Job", OPERATION_IDS("Validate-Job"))

case object CreateJob extends OperationType("Create-Job", OPERATION_IDS("Create-Job"))

case class GetJobAttributes(jobId: Int) extends OperationType("Get-Job-Attributes", OPERATION_IDS("Get-Job-Attributes"))

case class SendDocument(file: Source[ByteString, Future[IOResult]])
    extends OperationType("Send-Document", OPERATION_IDS("Send-Document"))
