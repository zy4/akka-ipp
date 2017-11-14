package de.envisia.akka.ipp.services

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.ActorSystem
import akka.http.scaladsl.{Http, HttpExt}
import akka.http.scaladsl.model.MediaType.NotCompressible
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{IOResult, KillSwitches, Materializer}
import akka.stream.scaladsl.Source
import akka.util.ByteString
import de.envisia.akka.ipp.Response.{
  GetJobAttributesResponse,
  GetPrinterAttributesResponse,
  IppResponse,
  PrintJobResponse
}
import de.envisia.akka.ipp._

import scala.reflect.runtime.universe._
import scala.concurrent.{ExecutionContext, Future}

class IPPClient(
    prefix: String,
    host: String,
    port: Int,
    queue: String = "print",
    username: Option[String],
    http: HttpExt
)(
    implicit mat: Materializer,
    val ec: ExecutionContext
) extends HttpRequestService {

  private val killSwitch = KillSwitches.shared("printer")

  private val atomicInt = new AtomicInteger(0)

  private def getRequestId: Int =
    atomicInt.updateAndGet(
      x => if (x + 1 == Int.MaxValue) 1 else x + 1
    )

  private val ippContentType = ContentType(MediaType.customBinary("application", "ipp", NotCompressible))

  def printJob(data: ByteString): Future[PrintJobResponse] =
    dispatch[Response.PrintJobResponse](PrintJob(data))

  def printerAttributes(): Future[GetPrinterAttributesResponse] =
    dispatch[GetPrinterAttributesResponse](GetPrinterAttributes)

  def validateJob(): Future[_] =
    dispatch(ValidateJob)

  def createJob(): Future[_] =
    dispatch(CreateJob)

  def sendDocument(file: Source[ByteString, Future[IOResult]]): Future[_] =
    dispatch(SendDocument(file))

  def getJobAttributes[T <: IppResponse](jobId: Int): Future[GetJobAttributesResponse] =
    dispatch[GetJobAttributesResponse](GetJobAttributes(jobId))

  def poll(jobId: Int): Future[Response.JobData] =
    new PollingService(this, killSwitch).poll(jobId)

  final protected[services] def dispatch[A <: IppResponse](ev: OperationType)(implicit tag: TypeTag[A]): Future[A] = {

    val service = new RequestService("ipp://" + host, queue = queue, requestId = getRequestId)

    val body = ev match {

      case PrintJob(data) =>
        Source.single(service.printJob(PrintJob(data).operationId, data))

      case GetPrinterAttributes =>
        Source.single(service.getPrinterAttributes(GetPrinterAttributes.operationId))

      case ValidateJob =>
        Source.single(service.validateJob(ValidateJob.operationId))

      case CreateJob =>
        Source.single(service.createJob(CreateJob.operationId))

      case SendDocument(file) =>
        Source.single(service.sendDocument(SendDocument(file).operationId)).concat(file)

      case GetJobAttributes(jobId) =>
        println("GetJobAttributes")
        Source.single(service.getJobAttributes(GetJobAttributes(jobId).operationId, jobId))

    }

    val ntt = HttpEntity(ippContentType, body)

    val request = HttpRequest(HttpMethods.POST, uri = s"$prefix://$host:$port", entity = ntt)

    val response = this.execute(request)
    val result = response.flatMap {
      case HttpResponse(StatusCodes.OK, headers, entity, _) =>
        Unmarshal(entity).to[ByteString]

      case resp => Future.failed(new Exception(s"Unexpected status code ${resp.status}"))
    }

    result.map(bs => new Response(bs).getResponse[A](ev))

  }

  override def execute(request: HttpRequest): Future[HttpResponse] = http.singleRequest(request)

  def shutdown(): Future[Unit] =
    Future.successful(killSwitch.shutdown())

}
