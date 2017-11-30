package de.envisia.akka.ipp.request

import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import scala.reflect.runtime.universe._
import akka.util.ByteString
import de.envisia.akka.ipp.attributes.Attributes.{ATTRIBUTE_GROUPS, IPP_VERSION, RESERVED}
import de.envisia.akka.ipp.request.RequestBuilder.Request.{CancelJob, GetPrinterAttributes}

private[request] class RequestSerializer(attributes: Map[String, (Byte, String)] = Map.empty[String, (Byte, String)]) {

  private implicit val bO: ByteOrder = ByteOrder.BIG_ENDIAN

  // generic byte strings
  @inline protected[request] final def putHeader(operationId: Byte, requestId: Int): ByteString =
    ByteString.newBuilder
      .putBytes(Array(IPP_VERSION, RESERVED))
      .putBytes(Array(RESERVED, operationId))
      .putInt(requestId)
      .putByte(ATTRIBUTE_GROUPS("operation-attributes-tag"))
      .result()
  @inline protected[request] final def putAttribute(name: String): ByteString =
    attributes(name) match {
      case (byte, value) =>
        ByteString.newBuilder
          .putByte(byte)
          .putShort(name.length)
          .putBytes(name.getBytes(StandardCharsets.UTF_8))
          .putShort(value.length)
          .putBytes(value.getBytes(StandardCharsets.UTF_8))
          .result()
    }

  /**
    * method for inserting the jobId
    * @param name
    * @return
    */
  @inline protected[request] final def putInteger(name: String): ByteString =
    attributes(name) match {
      case (byte, value) =>
        ByteString.newBuilder
          .putByte(byte)
          .putShort(name.length)
          .putBytes(name.getBytes(StandardCharsets.UTF_8))
          .putShort(4) // MAX INT
          .putInt(value.toInt)
          .result()
    }
  @inline protected[request] val putEnd: ByteString =
    ByteString.newBuilder
      .putByte(ATTRIBUTE_GROUPS("end-of-attributes-tag"))
      .result()

  protected[request] def serialize[A](oid: Byte, reqId: Int)(implicit tag: TypeTag[A]): ByteString = {
    val base = putHeader(oid, reqId) ++
      putAttribute("attributes-charset") ++
      putAttribute("attributes-natural-language") ++
      putAttribute("printer-uri")
    tag match {
      case t if t == typeTag[CancelJob] =>
        base ++ putAttribute("job-uri") ++ putAttribute("requesting-user-name") ++ putEnd
      case t if t == typeTag[GetPrinterAttributes] => base ++ putEnd
      case t if t == typeTag[RequestBuilder.Request.PrintJob] =>
        base ++
          putAttribute("requesting-user-name") ++
          putAttribute("job-name") ++
          putAttribute("document-format") ++ putEnd
      case t if t == typeTag[RequestBuilder.Request.GetJobAttributes] =>
        base ++ putInteger("job-id") ++ putAttribute("requesting-user-name") ++ putEnd
      case _ => throw new IllegalArgumentException("wrong request type")
    }

  }

}
