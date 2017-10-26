package de.envisia.status

object States {

  final val PRINTER_STATE: Map[String, Byte] = Map(
    "idle"       -> 0x03.toByte,
    "processing" -> 0x04.toByte,
    "stopped"    -> 0x05.toByte
  )

  final val JOB_STATE: Map[String, Byte] = Map(
    "pending"            -> 0x03.toByte,
    "pending-held"       -> 0x04.toByte,
    "processing"         -> 0x05.toByte,
    "processing-stopped" -> 0x06.toByte,
    "canceled"           -> 0x07.toByte,
    "aborted"            -> 0x08.toByte,
    "completed"          -> 0x09.toByte
  )

  final val DOCUMENT_STATE: Map[String, Byte] = Map(
    "pending"    -> 0x03.toByte,
    "processing" -> 0x04.toByte,
    "cancelled"  -> 0x05.toByte,
    "aborted"    -> 0x06.toByte,
    "completed"  -> 0x07.toByte
  )

}
