package de.envisia.akka.ipp.request

import akka.util.ByteString
import de.envisia.akka.ipp.attributes.Attributes._
import de.envisia.akka.ipp.request.RequestBuilder.IppRequest
import de.envisia.akka.ipp.request.RequestBuilder.Request._

import scala.reflect.runtime.universe._

private[ipp] class RequestBuilder[T <: RequestBuilder.Request](
    attributes: Map[String, (Byte, String)] = Map.empty[String, (Byte, String)]
) {

  /**
    * common setters
    */
  def setCharset(charset: String): RequestBuilder[T with Charset] = {
    val tpl = (ATTRIBUTE_TAGS("attributes-charset"), charset)
    new RequestBuilder(attributes + ("attributes-charset" -> tpl))
  }
  def setUri(uri: String): RequestBuilder[T with PrinterUri] = {
    val tpl = (ATTRIBUTE_TAGS("printer-uri"), uri)
    new RequestBuilder(attributes + ("printer-uri" -> tpl))
  }
  def setLanguage(lang: String): RequestBuilder[T with Language] = {
    val tpl = (ATTRIBUTE_TAGS("attributes-natural-language"), lang)
    new RequestBuilder(attributes + ("attributes-natural-language" -> tpl))
  }

  def setJobUri(jobUri: String): RequestBuilder[T with JobUri] = {
    val tpl = (ATTRIBUTE_TAGS("job-uri"), jobUri)
    new RequestBuilder(attributes + ("job-uri" -> tpl))
  }

  def setUser(user: String): RequestBuilder[T with User] = {
    val tpl = (ATTRIBUTE_TAGS("requesting-user-name"), user)
    new RequestBuilder(attributes + ("requesting-user-name" -> tpl))
  }
  def setJobName(jobName: String): RequestBuilder[T with JobName] = {
    val tpl = (ATTRIBUTE_TAGS("job-name"), jobName)
    new RequestBuilder(attributes + ("job-name" -> tpl))
  }
  def setFormat(format: String): RequestBuilder[T with Format] = {
    val tpl = (ATTRIBUTE_TAGS("document-format"), format)
    new RequestBuilder(attributes + ("document-format" -> tpl))
  }
  def askWithJobId(jobId: Int): RequestBuilder[T with JobId] = {
    val tpl = (ATTRIBUTE_TAGS("job-id"), jobId.toString)
    new RequestBuilder(attributes + ("job-id" -> tpl))
  }

  /**
    *  more general setters
    */
  def addOperationAttribute(tag: Byte, name: String, value: String): RequestBuilder[T with OperationAttribute] = {
    val tpl = (tag, value)
    new RequestBuilder[T with OperationAttribute](attributes + (name -> tpl))
  }

  def addJobAttribute(tag: Byte, name: String, value: String): RequestBuilder[T with JobAttribute] = {
    val tpl = (tag, value)
    new RequestBuilder[T with JobAttribute](attributes + (name -> tpl))
  }

  def build[A](oid: Byte, reqId: Int)(implicit tag: TypeTag[A]): IppRequest = {
    val serializer = new RequestSerializer(attributes)
    val result     = serializer.serialize[A](oid, reqId)
    new IppRequest(result)
  }

}

object RequestBuilder {

  protected[RequestBuilder] final class IppRequest(val request: ByteString) extends AnyVal

  sealed trait Request

  object Request {

    sealed trait EmptyRequest       extends Request
    sealed trait Charset            extends Request
    sealed trait Language           extends Request
    sealed trait PrinterUri         extends Request
    sealed trait User               extends Request
    sealed trait JobName            extends Request
    sealed trait Format             extends Request
    sealed trait JobAttribute       extends Request
    sealed trait OperationAttribute extends Request
    sealed trait JobId              extends Request
    sealed trait JobUri             extends Request

    type GetPrinterAttributes = EmptyRequest with Charset with Language with PrinterUri
    type CancelJob            = EmptyRequest with Charset with Language with PrinterUri with User with JobUri
    type PrintJob             = EmptyRequest with Charset with Language with PrinterUri with User with JobName with Format
    type GetJobAttributes     = EmptyRequest with Charset with Language with PrinterUri with User with JobId

  }

}
