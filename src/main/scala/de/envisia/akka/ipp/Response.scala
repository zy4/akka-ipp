package de.envisia.akka.ipp

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

import akka.util.ByteString
import de.envisia.akka.ipp.Response._
import de.envisia.akka.ipp.attributes.Attributes._
import de.envisia.akka.ipp.util.IppHelper

import scala.reflect.runtime.universe._
import scala.annotation.tailrec

class Response(bs: ByteString) {

  def getResponse[A <: IppResponse](o: OperationType)(implicit tTag: TypeTag[A]): A = {
    val bb      = bs.asByteBuffer
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

        val value = attrTag match {
          case b if !NUMERIC_TAGS.contains(b) =>
            new String(IppHelper.fromBuffer(bb, shortLenValue), StandardCharsets.UTF_8)
          case _ => ByteBuffer.wrap(IppHelper.fromBuffer(bb, shortLenValue)).getInt.toString
        }

        val tag = attributes.get(name)

        parseAttributes(newGroup, attributes + (name -> tag.map(v => value :: v).getOrElse(value :: Nil)))
      }
    }

    val attrs = parseAttributes(0x01.toByte, Map.empty) //TODO group byte? groupbyte not yet used

    val result = o.operationId match {
      case x if x == OPERATION_IDS("Get-Printer-Attributes") =>
        GetPrinterAttributesResponse(o.operationId, version, statusCode, requestId, attrs)
      case x if x == OPERATION_IDS("Print-Job") =>
        PrintJobResponse(
          o.operationId,
          version,
          statusCode,
          requestId,
          attrs,
          JobData(
            attrs("job-id").head.toInt,
            attrs("job-state").head.toInt,
            attrs("job-uri").head,
            attrs("job-state-reasons"),
            attrs("number-of-intervening-jobs").head.toInt
          )
        )
      case x if x == OPERATION_IDS("Get-Job-Attributes") =>
        GetJobAttributesResponse(
          o.operationId,
          version,
          statusCode,
          requestId,
          attrs,
          JobData(
            attrs("job-id").head.toInt,
            attrs("job-state").head.toInt,
            attrs("job-uri").head,
            attrs("job-state-reasons"),
            attrs("number-of-intervening-jobs").head.toInt
          )
        )
    }

    println(result)
    println(attrs.size)

    typeOf[A] match {
      case t if t =:= typeOf[GetPrinterAttributesResponse] => result.asInstanceOf[A]
      case t if t =:= typeOf[GetJobAttributesResponse]     => result.asInstanceOf[A]
      case t if t =:= typeOf[PrintJobResponse]             => result.asInstanceOf[A]
      case _                                               => throw new IllegalStateException("wrong response type found")
    }

  }

}

object Response {

  trait IppResponse

  case class GetPrinterAttributesResponse(
      oid: Byte,
      version: Short,
      statusCode: Short,
      requestId: Int,
      attributes: Map[String, List[String]]
  ) extends IppResponse

  case class GetJobAttributesResponse(
      oid: Byte,
      version: Short,
      statusCode: Short,
      requestId: Int,
      attributes: Map[String, List[String]],
      jobData: JobData
  ) extends IppResponse

  case class PrintJobResponse(
      oid: Byte,
      version: Short,
      statusCode: Short,
      requestId: Int,
      attributes: Map[String, List[String]],
      jobData: JobData
  ) extends IppResponse

  case class JobData(
      jobID: Int,
      jobState: Int,
      jobURI: String,
      jobStateReasons: List[String],
      numberOfInterveningJobs: Int
  )

}
