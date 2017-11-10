package de.envisia.akka.ipp.status

object StatusCodes {

  final val STATUS_CODES: Map[String, Byte] = Map(
    "successful-ok"                                     -> 0x0000.toByte,
    "successful-ok-ignored-or-substituted-attributes"   -> 0x0001.toByte,
    "successful-ok-conflicting-attributes"              -> 0x0002.toByte,
    "successful-ok-ignored-subscriptions"               -> 0x0003.toByte,
    "successful-ok-ignored-notifications"               -> 0x0004.toByte,
    "successful-ok-too-many-events"                     -> 0x0005.toByte,
    "successful-ok-but-cancel-subscription"             -> 0x0006.toByte,
    "successful-ok-events-complete"                     -> 0x0007.toByte,
    "client-error-bad-request"                          -> 0x0400.toByte,
    "client-error-forbidden"                            -> 0x0401.toByte,
    "client-error-not-authenticated"                    -> 0x0402.toByte,
    "client-error-not-authorized"                       -> 0x0403.toByte,
    "client-error-not-possible"                         -> 0x0404.toByte,
    "client-error-timeout"                              -> 0x0405.toByte,
    "client-error-not-found"                            -> 0x0406.toByte,
    "client-error-gone"                                 -> 0x0407.toByte,
    "client-error-request-entity-too-large"             -> 0x0408.toByte,
    "client-error-request-value-too-long"               -> 0x0409.toByte,
    "client-error-document-format-not-supported"        -> 0x040A.toByte,
    "client-error-attributes-or-values-not-supported"   -> 0x040B.toByte,
    "client-error-uri-scheme-not-supported"             -> 0x040C.toByte,
    "client-error-charset-not-supported"                -> 0x040D.toByte,
    "client-error-conflicting-attributes"               -> 0x040E.toByte,
    "client-error-compression-not-supported"            -> 0x040F.toByte,
    "client-error-compression-error"                    -> 0x0410.toByte,
    "client-error-document-format-error"                -> 0x0411.toByte,
    "client-error-document-access-error"                -> 0x0412.toByte,
    "client-error-attributes-not-settable"              -> 0x0413.toByte,
    "client-error-ignored-all-subscriptions"            -> 0x0414.toByte,
    "client-error-too-many-subscriptions"               -> 0x0415.toByte,
    "client-error-ignored-all-notifications"            -> 0x0416.toByte,
    "client-error-client-print-support-file-not-found"  -> 0x0417.toByte,
    "client-error-document-password-error"              -> 0x0418.toByte,
    "client-error-document-permission-error"            -> 0x0419.toByte,
    "client-error-document-security-error"              -> 0x041A.toByte,
    "client-error-document-unprintable-error"           -> 0x041B.toByte,
    "server-error-internal-error"                       -> 0x0500.toByte,
    "server-error-operation-not-supported"              -> 0x0501.toByte,
    "server-error-service-unavailable"                  -> 0x0502.toByte,
    "server-error-version-not-supported"                -> 0x0503.toByte,
    "server-error-device-error"                         -> 0x0504.toByte,
    "server-error-temporary-error"                      -> 0x0505.toByte,
    "server-error-not-accepting-jobs"                   -> 0x0506.toByte,
    "server-error-busy"                                 -> 0x0507.toByte,
    "server-error-job-canceled"                         -> 0x0508.toByte,
    "server-error-multiple-document-jobs-not-supported" -> 0x0509.toByte,
    "server-error-printer-is-deactivated"               -> 0x050A.toByte,
    "server-error-too-many-jobs"                        -> 0x050B.toByte,
    "server-error-too-many-documents"                   -> 0x050C.toByte
  )

  val reverseCodes: Map[Byte, String] = for ((k,v) <- STATUS_CODES) yield (v, k)

  final def getStatusMessage(statusCode: Byte): Option[String] = reverseCodes.get(statusCode)

}
