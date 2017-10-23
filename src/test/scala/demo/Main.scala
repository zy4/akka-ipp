package demo

import java.nio.{ ByteBuffer, ByteOrder }
import java.nio.charset.StandardCharsets

import akka.actor.ActorSystem
import akka.http.scaladsl.model.MediaType.NotCompressible
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ ActorMaterializer, Materializer }
import akka.util.ByteString
import de.envisia.services.IPPClient

import scala.concurrent.{ Await, ExecutionContext, Future }
import scala.concurrent.duration._

object Main {

  def fromBuffer(buf: ByteBuffer, length: Int): Array[Byte] = {
    val bytes = new Array[Byte](length)
    buf.get(bytes, 0, length)
    bytes
  }

  implicit val bO: ByteOrder = ByteOrder.BIG_ENDIAN

  var attributesMap: Map[String, String] = Map.empty
  val test: ByteString =
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
      .putShort("ipp://192.168.179.149:631/ipp".length)
      .putBytes("ipp://192.168.179.149:631/ipp".getBytes(StandardCharsets.UTF_8))
      // end attribute
      .putByte(0x03.toByte) // stop operation group
      .result()

//  val testArr = Array[Byte](192.toByte, 168.toByte, "", 1, 9)
  def bytes2hex(bytes: Array[Byte], sep: Option[String] = None): String =
    sep match {
      case None => bytes.map("%02x".format(_)).mkString
      case _    => bytes.map("%02x".format(_)).mkString(sep.get)
    }
  // bytes.foreach(println)

  def main(args: Array[String]): Unit = {

    implicit val actorSystem: ActorSystem           = ActorSystem()
    implicit val mat: Materializer                  = ActorMaterializer()
    implicit val executionContext: ExecutionContext = actorSystem.dispatcher

    val test2 = bytes2hex("attributes-charset".getBytes())

    println(test2)

    val client = new IPPClient("192.168.179.149", 631, "", Some(""), Some(""))(actorSystem, mat)

    val ippContentType = ContentType(MediaType.customBinary("application", "ipp", NotCompressible))
    /*ByteString.newBuilder
      .putBytes("Gattrbitues-charsetutf-8".getBytes(StandardCharsets.UTF_8))
  .putByte(0).putByte(0) */

    val request = HttpRequest(HttpMethods.POST, uri = "http://192.168.179.149:631/ipp")
      .withEntity(ippContentType, test)

    val response = client.execute(request)

    val result = response.flatMap {
      case HttpResponse(StatusCodes.OK, headers, entity, _) =>
        Unmarshal(entity).to[ByteString]

      case x => Future.failed(new Exception(s"Unexpected status code ${x.status}"))
    }

    val x = Await.result(result, 10.seconds)

    val bb                  = x.asByteBuffer
    val version             = bb.getShort
    val statusCode          = bb.getShort
    val requestId           = bb.getInt
    println(s"Request ID: $requestId")

    var isEnd    = bb.get
    var position = bb.position()

    while (isEnd != 0x03.toByte) {
      bb.position(position)

      // attribute
      val tag = bb.getChar()
      println(s"Tag: $tag")

      // Name
      val shortLenName = bb.getShort()
      println(s"Name Len: $shortLenName")
      val name = new String(fromBuffer(bb, shortLenName), StandardCharsets.UTF_8)

      // Value
      val shortLenValue = bb.getShort()
      val value = new String(fromBuffer(bb, shortLenValue), StandardCharsets.UTF_8)

      println(s"Name: $name - Value: $value")

      position = bb.position()
      isEnd = bb.get

    }

    case class IppResponse(version: Short, statusCode: Short, requestId: Int, attributes: Map[String, String])

    // first attribute
    //http.shutdownAllConnectionPools()
    //actorSystem.terminate()

  }

}
