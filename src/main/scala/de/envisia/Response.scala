package de.envisia

import java.nio.charset.StandardCharsets
import akka.util.ByteString
import de.envisia.Response.IppResponse
import de.envisia.util.IppHelper
import de.envisia.attributes.Attributes._

import scala.annotation.tailrec

class Response(x: ByteString) {

  def getResponse: IppResponse = {

    val bb      = x.asByteBuffer
    val version = Array(bb.get, bb.get)(0)
    println(s"Version: $version")
    val statusCode = bb.getShort
    val requestId  = bb.getInt
    println(s"Request ID: $requestId")

    @tailrec
    def parseAttributes(groupByte: Byte, attributes: Map[String, List[String]]): Map[String, List[String]] = {
      val byte = bb.get()

      if (byte == ATTRIBUTE_GROUPS("end-of-attributes-tag")) {
        attributes
      } else {
        val (newGroup, attrTag) = {
          if ((0 to 5).contains(byte.toInt)) { // delimiter tag values: https://tools.ietf.org/html/rfc8010#section-3.5.1
            // group
            val newGroup = byte
            // attribute tag
            val attr = bb.get()
            (newGroup, attr)
          } else {
            // attribute tag
            (groupByte, byte)
          }
        }

        // name
        val shortLenName = bb.getShort()
        val name         = new String(IppHelper.fromBuffer(bb, shortLenName), StandardCharsets.UTF_8)

        // value
        val shortLenValue = bb.getShort()
        val value         = new String(IppHelper.fromBuffer(bb, shortLenValue), StandardCharsets.UTF_8)

        val tag = attributes.get(name)

        parseAttributes(newGroup, attributes + (name -> tag.map(v => value :: v).getOrElse(value :: Nil)))
      }
    }

    val attrs = parseAttributes(0x01.toByte, Map.empty) //TODO group byte?

    val result = IppResponse(version, statusCode, requestId, attrs)

    println(result)
    println(attrs.size)

    result
  }

}

object Response {

  case class IppResponse(version: Short, statusCode: Short, requestId: Int, attributes: Map[String, List[String]])

}
