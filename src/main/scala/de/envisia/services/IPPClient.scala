package de.envisia.services

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.MediaType.NotCompressible
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{IOResult, Materializer}
import akka.stream.scaladsl.Source
import akka.util.ByteString
import de.envisia.{GetPrinterAttributes, OperationType, PrintJob, Response}

import scala.concurrent.{ExecutionContext, Future}

class IPPClient(
    prefix: String,
    host: String,
    port: Int,
    queue: String, // ????
    username: Option[String],
    password: Option[String]
)(
    implicit actorSystem: ActorSystem,
    mat: Materializer
) extends HttpRequestService {

  /*def poll() = {

    Future {
    mat.scheduleOnce(5.seconds, () => {
      poll()
    })
    }
  } */

  private val atomicInt = new AtomicInteger(0)

  private def getRequestId: Int =
    atomicInt.updateAndGet(
      x => if (x + 1 == Int.MaxValue) 1 else x + 1
    )

  override implicit val ec: ExecutionContext = actorSystem.dispatcher

  private val ippContentType = ContentType(MediaType.customBinary("application", "ipp", NotCompressible))

  def printJob(file: Source[ByteString, Future[IOResult]]): Future[Response.IppResponse] =
    dispatch(PrintJob(file))

  def printerAttributes(): Future[Response.IppResponse] =
    dispatch(GetPrinterAttributes)

  final protected def dispatch(ev: OperationType): Future[Response.IppResponse] = {

    val service = new RequestService("ipp://" + host, requestId = getRequestId)

    val body = ev match {

      case PrintJob(file) =>
        Source.single(service.printJob(PrintJob(file).operationId)).concat(file)

      case GetPrinterAttributes =>
        Source.single(service.getPrinterAttributes(GetPrinterAttributes.operationId))

    }

    val ntt = HttpEntity(ippContentType, body)

    val request = HttpRequest(HttpMethods.POST, uri = s"$prefix://$host:$port", entity = ntt)

    val response = this.execute(request)
    val result = response.flatMap {
      case HttpResponse(StatusCodes.OK, headers, entity, _) =>
        Unmarshal(entity).to[ByteString]

      case resp => Future.failed(new Exception(s"Unexpected status code ${resp.status}"))
    }

    result.map(bs => new Response(bs).getResponse)

  }

  override def execute(request: HttpRequest): Future[HttpResponse] = Http().singleRequest(request)

}
