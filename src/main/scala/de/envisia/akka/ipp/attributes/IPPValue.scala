package de.envisia.akka.ipp.attributes

sealed trait IPPValue

object IPPValue {

  case class NumericVal(value: Int) extends IPPValue

  case class TextVal(value: String) extends IPPValue

  case class Unstructured(value: Array[Byte]) extends IPPValue

}
