package de.envisia

import java.nio.charset.StandardCharsets
import akka.util.ByteString
import de.envisia.Response.IppResponse
import de.envisia.util.IppHelper

import scala.annotation.tailrec

class Response(x: ByteString) {

  @volatile private var attributesMap: Map[String, String] = Map.empty

  def getResponse: IppResponse = {

    val bb         = x.asByteBuffer
    val version    = bb.getShort // TODO 512 ?
    val statusCode = bb.getShort
    val requestId  = bb.getInt
    println(s"Request ID: $requestId")

    bb.get
    var position = bb.position()

    // TODO cannot pass position 

    @tailrec
    def parseAttributes(isE: Byte, posi: Int): Unit =
      isE match {
        case pos if pos == 0x03.toByte => println("end of response")
        case a =>
          println("POSI" + position)
          if (a != 0x04.toByte)
            bb.position(position)
          // attribute
          val tag = bb.get
          println(s"Tag: $tag")

          // Name
          val shortLenName = bb.getShort()
          println(s"Name Len: $shortLenName")
          val name = new String(IppHelper.fromBuffer(bb, shortLenName), StandardCharsets.UTF_8)

          // Value
          val shortLenValue = bb.getShort()
          val value         = new String(IppHelper.fromBuffer(bb, shortLenValue), StandardCharsets.UTF_8)

          position = bb.position()

          attributesMap += (name -> value)
          parseAttributes(bb.get, bb.position())

      }

    parseAttributes(bb.get, bb.position())

    val result = IppResponse(version, statusCode, requestId, attributesMap)
    println(result)
    println(attributesMap.size)
    result
  }

}

object Response {
  case class IppResponse(version: Short, statusCode: Short, requestId: Int, attributes: Map[String, String])
}
