package de.envisia.akka.ipp.request

import akka.util.ByteString
import de.envisia.akka.ipp.attributes.Attributes._
import de.envisia.akka.ipp.request.RequestBuilder.Request._

final class IppRequest(val request: ByteString) extends AnyVal

class RequestBuilder[T <: RequestBuilder.Request](
    attributes: Map[String, (Byte, String)] = Map.empty[String, (Byte, String)]
) {

  /**
    * common setters
    */
  def setCharset(charset: String): RequestBuilder[T with Charset] =
    new RequestBuilder(attributes + ("attributes-charset" -> (ATTRIBUTE_TAGS("attributes-charset"), charset)))

  def setUri(uri: String): RequestBuilder[T with PrinterUri] =
    new RequestBuilder(attributes + ("printer-uri" -> (ATTRIBUTE_TAGS("printer-uri"), uri)))

  def setLanguage(lang: String): RequestBuilder[T with Language] =
    new RequestBuilder(
      attributes + ("attributes-natural-language" -> (ATTRIBUTE_TAGS("attributes-natural-language"), lang))
    )

  def setUser(user: String): RequestBuilder[T with User] =
    new RequestBuilder(attributes + ("requesting-user-name" -> (ATTRIBUTE_TAGS("requesting-user-name"), user)))

  def setJobName(jobName: String): RequestBuilder[T with JobName] =
    new RequestBuilder(attributes + ("job-name" -> (ATTRIBUTE_TAGS("job-name"), jobName)))

  def setFormat(format: String): RequestBuilder[T with Format] =
    new RequestBuilder(attributes + ("document-format" -> (ATTRIBUTE_TAGS("document-format"), format)))

  def askWithJobId(jobId: Int): RequestBuilder[T with JobId] =
    new RequestBuilder(attributes + ("job-id" -> (ATTRIBUTE_TAGS("job-id"), jobId.toString)))

  /**
    *  more general setters
    */
  def addOperationAttribute(tag: Byte, name: String, value: String): RequestBuilder[T with OperationAttribute] =
    new RequestBuilder[T with OperationAttribute](attributes + (name -> (tag, value)))

  def addJobAttribute(tag: Byte, name: String, value: String): RequestBuilder[T with JobAttribute] =
    new RequestBuilder[T with JobAttribute](attributes + (name -> (tag, value)))

  // TODO try to replace reflection with the AUX pattern if possible

  import scala.reflect.runtime.universe._

  def build[A](oid: Byte, reqId: Int)(implicit tag: TypeTag[A]): IppRequest = {

    val serializer = new RequestSerializer(this.attributes)

    val result = serializer.serialize[A](oid, reqId)

    new IppRequest(result)
  }

}

object RequestBuilder {

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

    //type MinimalRequest = EmptyRequest
    type GetPrinterAttributes = EmptyRequest with Charset with Language with PrinterUri
    type PrintJob             = EmptyRequest with Charset with Language with PrinterUri with User with JobName with Format
    type ValidateJob          = EmptyRequest with Charset with Language with PrinterUri with User with JobName with Format
    type GetJobAttributes     = EmptyRequest with Charset with Language with PrinterUri with User with JobId
    type CreateJob            = EmptyRequest with Charset with Language with PrinterUri with User with JobName with Format
    type SendDocument         = EmptyRequest with Charset with Language with PrinterUri with User with JobName with Format

  }

}
