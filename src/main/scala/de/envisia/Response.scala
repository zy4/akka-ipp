package de.envisia

import java.nio.ByteBuffer
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

        /* val value =
          case class Attribute(array: Array[Byte]) {
            def getInt: Int = ByteBuffer.wrap(array).getInt()
            def getString: String = new String(array, StandardCharsets.UTF_8)
          } */

        val value = attrTag match {
          case b if !NUMERIC_TAGS.contains(b) =>
            new String(IppHelper.fromBuffer(bb, shortLenValue), StandardCharsets.UTF_8)
          case _ => ByteBuffer.wrap(IppHelper.fromBuffer(bb, shortLenValue)).getInt.toString
        }

        val tag = attributes.get(name)

        parseAttributes(newGroup, attributes + (name -> tag.map(v => value :: v).getOrElse(value :: Nil)))
      }
    }

    val printerAttrs = parseAttributes(0x01.toByte, Map.empty) //TODO group byte? groupbyte not yet used

    val result = IppResponse(version, statusCode, requestId, printerAttrs)

    println(result)
    println(printerAttrs.size)
    print("jobstate ..." + printerAttrs("job-state"))
    print("jobid ..." + printerAttrs("job-id"))

    result
  }

}

object Response {

  case class IppResponse(
      version: Short,
      statusCode: Short,
      requestId: Int,
      printerAttributes: Map[String, List[String]]
  )

}
