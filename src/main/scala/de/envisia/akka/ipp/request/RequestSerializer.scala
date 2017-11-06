package de.envisia.akka.ipp.request

import java.nio.ByteOrder
import java.nio.charset.StandardCharsets

import akka.util.ByteString
import de.envisia.akka.ipp.attributes.Attributes.{ATTRIBUTE_GROUPS, IPP_VERSION, RESERVED}
import de.envisia.akka.ipp.request.RequestBuilder.Request.{CreateJob, GetPrinterAttributes, ValidateJob}
import de.envisia.akka.ipp.status.IppExceptions.WrongRequestType

class RequestSerializer(attributes: Map[String, (Byte, String)] = Map.empty[String, (Byte, String)]) {

  private implicit val bO: ByteOrder = ByteOrder.BIG_ENDIAN

  // generic byte strings
  @inline protected[request] final def putHeader(operationId: Byte, requestId: Int): ByteString =
    ByteString.newBuilder
      .putBytes(Array(IPP_VERSION, RESERVED))
      .putBytes(Array(RESERVED, operationId))
      .putInt(requestId) // TODO does not increment
      .putByte(ATTRIBUTE_GROUPS("operation-attributes-tag"))
      .result()
  @inline protected[request] final def putAttribute(name: String): ByteString =
    ByteString.newBuilder
      .putByte(attributes(name)._1)
      .putShort(name.length)
      .putBytes(name.getBytes(StandardCharsets.UTF_8))
      .putShort(attributes(name)._2.length)
      .putBytes(attributes(name)._2.getBytes(StandardCharsets.UTF_8))
      .result()

  /**
    * method for inserting the jobId
    * @param name
    * @return
    */
  @inline protected[request] final def putInteger(name: String): ByteString =
    ByteString.newBuilder
      .putByte(attributes(name)._1)
      .putShort(name.length)
      .putBytes(name.getBytes(StandardCharsets.UTF_8))
      .putShort(4) // MAX INT
      .putInt(attributes(name)._2.toInt)
      .result()
  @inline protected[request] val putEnd: ByteString =
    ByteString.newBuilder
      .putByte(ATTRIBUTE_GROUPS("end-of-attributes-tag"))
      .result()

  // TODO try to replace reflection with the AUX pattern if possible

  import scala.reflect.runtime.universe._

  protected[request] def serialize[A](oid: Byte, reqId: Int)(implicit tag: TypeTag[A]): ByteString = {

    val base = putHeader(oid, reqId) ++
      putAttribute("attributes-charset") ++
      putAttribute("attributes-natural-language") ++
      putAttribute("printer-uri")

    tag match {

      case t if t == typeTag[GetPrinterAttributes] => base ++ putEnd
      case t if t == typeTag[RequestBuilder.Request.PrintJob] | t == typeTag[ValidateJob] =>
        base ++
          putAttribute("requesting-user-name") ++
          putAttribute("job-name") ++
          putAttribute("document-format") ++ putEnd

      case t if t == typeTag[RequestBuilder.Request.GetJobAttributes] =>
        base ++ putInteger("job-id") ++ putAttribute("requesting-user-name") ++ putEnd

      case t if t == typeTag[CreateJob] =>
        base ++ putAttribute("requesting-user-name") ++ putAttribute("job-name") ++
          putAttribute("document-format") ++ putEnd
      case t if t == typeTag[RequestBuilder.Request.SendDocument] =>
        base ++ putAttribute("requesting-user-name") ++
          putAttribute("job-name") ++ putAttribute("document-format") ++ putEnd
      case _ => throw new WrongRequestType("Wrong Request Type")

    }

  }

}
