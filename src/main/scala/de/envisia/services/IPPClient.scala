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
import de.envisia.{GetPrinterAttributes, PrintJob, OperationType, Response}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

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

  private val atomicInt = new AtomicInteger(1)

  override implicit val ec: ExecutionContext = actorSystem.dispatcher

  private val ippContentType = ContentType(MediaType.customBinary("application", "ipp", NotCompressible))

  def printJob(file: Source[ByteString, Future[IOResult]]): Unit =
    dispatch(PrintJob(file))

  def printerAttributes(): Unit =
    dispatch(GetPrinterAttributes)

  final protected def dispatch(ev: OperationType): Unit = {

    val ntt = ev match {

      case PrintJob(file) =>
        val data = Source
          .single(
            new RequestService("ipp://" + host, requestId = atomicInt.incrementAndGet())
              .printJob(PrintJob(file).operationId)
          )
          .concat(file)
        HttpEntity(ippContentType, data)

      case GetPrinterAttributes =>
        val data = new RequestService("ipp://" + host, requestId = atomicInt.incrementAndGet())
          .getPrinterAttributes(GetPrinterAttributes.operationId)
        HttpEntity(ippContentType, data)

    }

    val request = HttpRequest(HttpMethods.POST, uri = s"$prefix://$host:$port", entity = ntt)

    val response = this.execute(request)
    val result = response.flatMap {
      case HttpResponse(StatusCodes.OK, headers, entity, _) =>
        Unmarshal(entity).to[ByteString]

      case resp => Future.failed(new Exception(s"Unexpected status code ${resp.status}"))
    }

    val x = Await.result(result, 10.seconds)

    val ippResponse = new Response(x).getResponse

  }

  override def execute(request: HttpRequest): Future[HttpResponse] = Http().singleRequest(request)

}
