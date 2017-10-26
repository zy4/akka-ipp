package de.envisia

object Constants {

  final val WELL_KNOWN_PORT = 631

  final val ATTRIBUTE_GROUPS: Map[String, Byte] = Map(
    "operation-attributes-tag"   -> 0x01.toByte,
    "job-attributes-tag"         -> 0x02.toByte,
    "end-of-attributes-tag"      -> 0x03.toByte,
    "printer-attributes-tag"     -> 0x04.toByte,
    "unsupported-attributes-tag" -> 0x05.toByte
  )

  final val OPERATION_IDS: Map[String, Byte] = Map(
    "Get-Printer-Attributes" -> 0x0b.toByte,
    "Print-Job"              -> 0x02.toByte,
    "Validate-Job"           -> 0x04.toByte,
    "Get-Job-Attributes"     -> 0x09.toByte
  )

}
