package de.envisia

import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

import akka.util.ByteString

class Request(uri: String) {

  implicit val bO: ByteOrder = ByteOrder.BIG_ENDIAN

  def getPrinterAttributes: ByteString =
    ByteString.newBuilder
      .putBytes(Array(0x02.toByte, 0x00.toByte))
      .putBytes(Array(0x00.toByte, 0x0b.toByte))
      .putInt(1)
      .putByte(0x01.toByte) // start operation group

      // start attribute
      .putByte(0x47.toByte)
      .putShort("attributes-charset".length)
      .putBytes("attributes-charset".getBytes(StandardCharsets.UTF_8))
      .putShort("utf-8".length)
      .putBytes("utf-8".getBytes(StandardCharsets.UTF_8))
      //
      .putByte(0x48.toByte)
      .putShort("attributes-natural-language".length)
      .putBytes("attributes-natural-language".getBytes(StandardCharsets.UTF_8))
      .putShort("de-de".length)
      .putBytes("de-de".getBytes(StandardCharsets.UTF_8))

      //printer
      .putByte(0x45.toByte)
      .putShort("printer-uri".length)
      .putBytes("printer-uri".getBytes(StandardCharsets.UTF_8))
      .putShort(s"${this.uri}:${Constants.WELL_KNOWN_PORT}/ipp/print".length)
      .putBytes(s"${this.uri}:${Constants.WELL_KNOWN_PORT}/ipp/print".getBytes(StandardCharsets.UTF_8))
      // end attribute
      .putByte(0x03.toByte) // stop operation group
      .result()

  def printJob: ByteString =
    ByteString.newBuilder
      .putBytes(Array(0x02.toByte, 0x00.toByte))
      .putBytes(Array(0x00.toByte, 0x02.toByte))
      .putInt(1)
      .putByte(0x01.toByte) // start operation group
      // start attribute
      // start attribute
      .putByte(0x47.toByte)
      .putShort("attributes-charset".length)
      .putBytes("attributes-charset".getBytes(StandardCharsets.UTF_8))
      .putShort("utf-8".length)
      .putBytes("utf-8".getBytes(StandardCharsets.UTF_8))
      .putByte(0x48.toByte)
      .putShort("attributes-natural-language".length)
      .putBytes("attributes-natural-language".getBytes(StandardCharsets.UTF_8))
      .putShort("de-de".length)
      .putBytes("de-de".getBytes(StandardCharsets.UTF_8))
      .putByte(0x45.toByte)
      .putShort("printer-uri".length)
      .putBytes("printer-uri".getBytes(StandardCharsets.UTF_8))
      .putShort(s"${this.uri}:${Constants.WELL_KNOWN_PORT}/ipp/print".length)
      .putBytes(s"${this.uri}:${Constants.WELL_KNOWN_PORT}/ipp/print".getBytes(StandardCharsets.UTF_8))
      .putByte(0x42.toByte)
      .putShort("requesting-user-name".length)
      .putBytes("requesting-user-name".getBytes(StandardCharsets.UTF_8))
      .putShort(s"zin".length)
      .putBytes(s"zin".getBytes(StandardCharsets.UTF_8))
      .putByte(0x42.toByte)
      .putShort("job-name".length)
      .putBytes("job-name".getBytes(StandardCharsets.UTF_8))
      .putShort(s"test".length)
      .putBytes(s"test".getBytes(StandardCharsets.UTF_8))
      .putByte(0x49.toByte)
      .putShort("document-format".length)
      .putBytes("document-format".getBytes(StandardCharsets.UTF_8))
      .putShort(s"application/octet-stream".length) // TODO change to application/octet-stream
      .putBytes(s"application/octet-stream".getBytes(StandardCharsets.UTF_8))
      .putByte(0x03.toByte) // stop operation group
      .putBytes(Files.readAllBytes(Paths.get("examples/pdf-sample.pdf")))
      .result()

  def createJob = ???

}
