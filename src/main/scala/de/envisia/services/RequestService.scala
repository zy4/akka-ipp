package de.envisia.services

import java.nio.ByteOrder
import java.nio.charset.StandardCharsets

import akka.util.ByteString
import de.envisia.{Constants, RequestBuilder}

class RequestService(uri: String, user: String = "anonymous", jobName: String = "") {

  implicit val bO: ByteOrder = ByteOrder.BIG_ENDIAN

  def getPrinterAttributes: ByteString =
    new RequestBuilder[RequestBuilder.Request.GetPrinterAttributes]()
      .setCharset("utf-8")
      .setUri(this.uri + s"${Constants.WELL_KNOWN_PORT}/ipp/print")
      .setLanguage("de-de")
      .buildGetPrinterAttr
      .request

  def printJob: ByteString =
    new RequestBuilder[RequestBuilder.Request.PrintJob]()
      .setCharset("utf-8")
      .setUri(this.uri + s"${Constants.WELL_KNOWN_PORT}/ipp/print")
      .setLanguage("de-de")
      .setUser(user)
      .setJobName(jobName)
      .setFormat("application/octet-stream")
      .buildPrintJob
      .request


  def validateJob: ByteString = ???

  def createJob: ByteString = ???

  def getStatus: ByteString = ???

}
