package de.envisia.services

import akka.util.ByteString
import de.envisia.{Constants, RequestBuilder}

class RequestService(
    uri: String,
    lang: String = "en-us",
    user: String = "dummy",
    jobName: String = "",
    charset: String = "utf-8",
    format: String = "application/octet-stream",
    requestId: Int = 1
) {

  def getPrinterAttributes: ByteString =
    new RequestBuilder[RequestBuilder.Request.GetPrinterAttributes]()
      .setCharset(charset)
      .setUri(this.uri + s"${Constants.WELL_KNOWN_PORT}/ipp/print")
      .setLanguage(lang)
      .buildGetPrinterAttr(requestId)
      .request

  def getJobAttributes(jobId: Int): ByteString =
    new RequestBuilder[RequestBuilder.Request.GetJobAttributes]()
      .setCharset(charset)
      .setUri(this.uri + s"${Constants.WELL_KNOWN_PORT}/ipp/print")
      .setLanguage(lang)
      .askWithJobId(jobId)
      .setUser(user)
      .buildGetJobAttr(requestId)
      .request

  def printJob: ByteString =
    new RequestBuilder[RequestBuilder.Request.PrintJob]()
      .setCharset(charset)
      .setUri(this.uri + s"${Constants.WELL_KNOWN_PORT}/ipp/print")
      .setLanguage(lang)
      .setUser(user)
      .setJobName(jobName)
      .setFormat(format)
      .buildPrintJob(requestId)
      .request

  def validateJob: ByteString =
    new RequestBuilder[RequestBuilder.Request.ValidateJob]()
      .setCharset(charset)
      .setUri(this.uri + s"${Constants.WELL_KNOWN_PORT}/ipp/print")
      .setLanguage(lang)
      .setUser(user)
      .setJobName(jobName)
      .setFormat(format)
      .buildPrintJob(requestId)
      .request

  def createJob: ByteString = ???

  def getStatus: ByteString = ???

}
