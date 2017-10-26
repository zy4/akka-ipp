package de.envisia.services

import akka.util.ByteString
import de.envisia.RequestBuilder
import de.envisia.attributes.Attributes._

class RequestService(
    uri: String,
    lang: String = "en-us",
    user: String = "dummy",
    jobName: String = "",
    charset: String = "utf-8",
    format: String = "application/octet-stream",
    requestId: Int = 1
) {

  def getPrinterAttributes(operationId: Byte): ByteString =
    new RequestBuilder[RequestBuilder.Request.GetPrinterAttributes]()
      .setCharset(charset)
      .setUri(this.uri + s"$WELL_KNOWN_PORT/ipp/print")
      .setLanguage(lang)
      .buildGetPrinterAttr(operationId, requestId)
      .request

  def getJobAttributes(operationId: Byte, jobId: Int): ByteString =
    new RequestBuilder[RequestBuilder.Request.GetJobAttributes]()
      .setCharset(charset)
      .setUri(this.uri + s"$WELL_KNOWN_PORT/ipp/print")
      .setLanguage(lang)
      .askWithJobId(jobId)
      .setUser(user)
      .buildGetJobAttr(operationId, requestId)
      .request

  def printJob(operationId: Byte): ByteString =
    new RequestBuilder[RequestBuilder.Request.PrintJob]()
      .setCharset(charset)
      .setUri(this.uri + s"$WELL_KNOWN_PORT/ipp/print")
      .setLanguage(lang)
      .setUser(user)
      .setJobName(jobName)
      .setFormat(format)
      .buildPrintJob(operationId, requestId)
      .request

  def validateJob(operationId: Byte): ByteString =
    new RequestBuilder[RequestBuilder.Request.ValidateJob]()
      .setCharset(charset)
      .setUri(this.uri + s"$WELL_KNOWN_PORT/ipp/print")
      .setLanguage(lang)
      .setUser(user)
      .setJobName(jobName)
      .setFormat(format)
      .buildPrintJob(operationId, requestId)
      .request

  def createJob: ByteString = ???

  def getStatus: ByteString = ???

}
