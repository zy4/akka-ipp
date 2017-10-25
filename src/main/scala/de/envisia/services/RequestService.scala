package de.envisia.services

import akka.util.ByteString
import de.envisia.{Constants, RequestBuilder}

class RequestService(
    uri: String,
    lang: String = "de-de",
    user: String = "anonymous",
    jobName: String = "",
    charset: String = "utf-8",
    format: String = "application/octet-stream"
) {

  def getPrinterAttributes: ByteString =
    new RequestBuilder[RequestBuilder.Request.GetPrinterAttributes]()
      .setCharset(charset)
      .setUri(this.uri + s"${Constants.WELL_KNOWN_PORT}/ipp/print")
      .setLanguage(lang)
      .buildGetPrinterAttr
      .request

  def getJobAttributes(jobId: Int): ByteString =
    new RequestBuilder[RequestBuilder.Request.GetJobAttributes]()
      .setCharset(charset)
      .setUri(this.uri + s"${Constants.WELL_KNOWN_PORT}/ipp/print")
      .setLanguage(lang)
      .askWithJobId(jobId)
      .setUser(user)
      .buildGetJobAttr
      .request

  def printJob: ByteString =
    new RequestBuilder[RequestBuilder.Request.PrintJob]()
      .setCharset(charset)
      .setUri(this.uri + s"${Constants.WELL_KNOWN_PORT}/ipp/print")
      .setLanguage(lang)
      .setUser(user)
      .setJobName(jobName)
      .setFormat(format)
      .buildPrintJob
      .request

  def validateJob: ByteString =
    new RequestBuilder[RequestBuilder.Request.ValidateJob]()
      .setCharset(charset)
      .setUri(this.uri + s"${Constants.WELL_KNOWN_PORT}/ipp/print")
      .setLanguage(lang)
      .setUser(user)
      .setJobName(jobName)
      .setFormat(format)
      .buildPrintJob
      .request

  def createJob: ByteString = ???

  def getStatus: ByteString = ???

}