package de.envisia

object Constants {

  final val WELL_KNOWN_PORT = 631

  final val ATTRIBUTE_GROUPS: Map[String, Byte] = Map(
    "job-attributes-tag"         -> 0x02.toByte,
    "end-of-attributes-tag"      -> 0x03.toByte,
    "printer-attributes-tag"     -> 0x04.toByte,
    "unsupported-attributes-tag" -> 0x05.toByte
  )

}
