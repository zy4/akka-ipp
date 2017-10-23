package de.envisia

import java.nio.charset.StandardCharsets

import akka.util.ByteString
import de.envisia.Response.IppResponse
import de.envisia.util.IppHelper

class Response(x: ByteString) {

  @volatile private var attributesMap: Map[String, String] = Map.empty

  def getResponse: IppResponse = {

    val bb         = x.asByteBuffer
    val version    = bb.getShort // TODO 512 ?
    val statusCode = bb.getShort
    val requestId  = bb.getInt
    println(s"Request ID: $requestId")

    var isEnd    = bb.get
    var position = bb.position()

    while (isEnd != 0x03.toByte /* || isEnd != 0x04.toByte*/ ) {
      if (isEnd != 0x04.toByte)
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

      //println(s"Name: $name - Value: $value")

      position = bb.position()
      isEnd = bb.get

      attributesMap += (name -> value)
    }

    val result = IppResponse(version, statusCode, requestId, attributesMap)
    println(result)
    result
  }

}

object Response {

  case class IppResponse(version: Short, statusCode: Short, requestId: Int, attributes: Map[String, String])
}
