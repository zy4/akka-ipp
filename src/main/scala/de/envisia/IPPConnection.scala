package de.envisia

import java.io.InputStream

sealed trait IPPConnection {

  def getStatusCode: Int
  def execute: Boolean
  def setIppRequest(stream: InputStream): Unit

}

final class IppHttpConnection extends IPPConnection {
  override def getStatusCode: Int                       = ???
  override def execute: Boolean                         = ???
  override def setIppRequest(stream: InputStream): Unit = ???
}
