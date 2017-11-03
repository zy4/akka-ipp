package de.envisia.akka.ipp.util

import java.nio.ByteBuffer

object IppHelper {

  def bytes2hex(bytes: Array[Byte], sep: Option[String] = None): String =
    sep match {
      case None => bytes.map("%02x".format(_)).mkString
      case _    => bytes.map("%02x".format(_)).mkString(sep.get)
    }

  def fromBuffer(buf: ByteBuffer, length: Int): Array[Byte] = {
    val bytes = new Array[Byte](length)
    buf.get(bytes, 0, length)
    bytes
  }

}
