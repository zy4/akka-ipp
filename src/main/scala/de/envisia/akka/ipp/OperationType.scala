package de.envisia.akka.ipp


import akka.util.ByteString
import de.envisia.akka.ipp.attributes.Attributes._

sealed abstract class OperationType(val name: String, val operationId: Byte)

private[ipp] object OperationType {

  case object GetPrinterAttributes
      extends OperationType("Get-Printer-Attributes", OPERATION_IDS("Get-Printer-Attributes"))

  case class PrintJob(data: ByteString) extends OperationType("Print-Job", OPERATION_IDS("Print-Job"))

  case class GetJobAttributes(jobId: Int)
      extends OperationType("Get-Job-Attributes", OPERATION_IDS("Get-Job-Attributes"))

  case class CancelJob(jobId: Int) extends OperationType("Cancel-Job", OPERATION_IDS("Cancel-Job"))

}
