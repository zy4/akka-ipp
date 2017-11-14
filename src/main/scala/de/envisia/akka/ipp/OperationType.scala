package de.envisia.akka.ipp

import akka.stream.IOResult
import akka.stream.scaladsl.Source
import akka.util.ByteString
import de.envisia.akka.ipp.attributes.Attributes._

import scala.concurrent.Future

sealed abstract class OperationType(val name: String, val operationId: Byte)

case object GetPrinterAttributes
    extends OperationType("Get-Printer-Attributes", OPERATION_IDS("Get-Printer-Attributes"))

case class PrintJob(data: ByteString) extends OperationType("Print-Job", OPERATION_IDS("Print-Job"))

case object ValidateJob extends OperationType("Validate-Job", OPERATION_IDS("Validate-Job"))

case object CreateJob extends OperationType("Create-Job", OPERATION_IDS("Create-Job"))

case class GetJobAttributes(jobId: Int) extends OperationType("Get-Job-Attributes", OPERATION_IDS("Get-Job-Attributes"))

case class CancelJob(jobId: Int) extends OperationType("Cancel-Job", OPERATION_IDS("Cancel-Job"))

case class SendDocument(file: Source[ByteString, Future[IOResult]])
    extends OperationType("Send-Document", OPERATION_IDS("Send-Document"))
