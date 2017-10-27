package de.envisia.attributes


object Attributes {

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

  final val ATTRIBUTE_TAGS: Map[String, Byte] = Map(
    "attributes-charset"          -> 0x47.toByte,
    "printer-uri"                 -> 0x45.toByte,
    "attributes-natural-language" -> 0x48.toByte,
    "requesting-user-name"        -> 0x42.toByte,
    "job-name"                    -> 0x42.toByte,
    "document-format"             -> 0x49.toByte,
    "job-id"                      -> 0x21.toByte
  )

  //attr tags Integer (0x21) && Enum (0x23)
  final val NUMERIC_TAGS: Seq[Byte] = Seq(0x21.toByte, 0x23.toByte)

}
