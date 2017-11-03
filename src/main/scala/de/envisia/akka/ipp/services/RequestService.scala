package de.envisia.akka.ipp.services

import akka.util.ByteString
import de.envisia.akka.ipp.RequestBuilder
import de.envisia.akka.ipp.RequestBuilder.Request._
import de.envisia.akka.ipp.attributes.Attributes._

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
    new RequestBuilder[GetPrinterAttributes]()
      .setCharset(charset)
      .setUri(this.uri + s"$WELL_KNOWN_PORT/ipp/print")
      .setLanguage(lang)
      .build[GetPrinterAttributes](operationId, requestId)
      .request

  def getJobAttributes(operationId: Byte, jobId: Int): ByteString =
    new RequestBuilder[GetJobAttributes]()
      .setCharset(charset)
      .setUri(this.uri + s"$WELL_KNOWN_PORT/ipp/print")
      .setLanguage(lang)
      .askWithJobId(jobId)
      .setUser(user)
      .build[RequestBuilder.Request.GetJobAttributes](operationId, requestId)
      .request

  def printJob(operationId: Byte): ByteString =
    new RequestBuilder[PrintJob]()
      .setCharset(charset)
      .setUri(this.uri + s"$WELL_KNOWN_PORT/ipp/print")
      .setLanguage(lang)
      .setUser(user)
      .setJobName(jobName)
      .setFormat(format)
      .build[RequestBuilder.Request.PrintJob](operationId, requestId)
      .request

  def validateJob(operationId: Byte): ByteString =
    new RequestBuilder[ValidateJob]()
      .setCharset(charset)
      .setUri(this.uri + s"$WELL_KNOWN_PORT/ipp/print")
      .setLanguage(lang)
      .setUser(user)
      .setJobName(jobName)
      .setFormat(format)
      .build[ValidateJob](operationId, requestId)
      .request

  def createJob(operationId: Byte): ByteString =
    new RequestBuilder[CreateJob]()
      .setCharset(charset)
      .setUri(this.uri + s"$WELL_KNOWN_PORT/ipp/print")
      .setLanguage(lang)
      .setUser(user)
      .setJobName(jobName)
      .setFormat(format)
      .build[CreateJob](operationId, requestId)
      .request

  def sendDocument(operationId: Byte): ByteString =
    new RequestBuilder[SendDocument]()
      .setCharset(charset)
      .setUri(this.uri + s"$WELL_KNOWN_PORT/ipp/print")
      .setLanguage(lang)
      .setUser(user)
      .setJobName(jobName)
      .setFormat(format)
      .build[SendDocument](operationId, requestId)
      .request

}
