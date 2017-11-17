package de.envisia.akka.ipp.services

import java.util.concurrent.atomic.AtomicInteger
import akka.http.scaladsl.HttpExt
import akka.http.scaladsl.model.MediaType.NotCompressible
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ KillSwitches, Materializer }
import akka.stream.scaladsl.Source
import akka.util.ByteString
import de.envisia.akka.ipp.OperationType._
import de.envisia.akka.ipp.Response._
import de.envisia.akka.ipp._
import de.envisia.akka.ipp.model.IppConfig
import org.slf4j.LoggerFactory

import scala.reflect.runtime.universe._
import scala.concurrent.{ ExecutionContext, Future }


class IPPClient(http: HttpExt)(
    implicit mat: Materializer,
    val ec: ExecutionContext
) {

  private val logger = LoggerFactory.getLogger(getClass)
  logger.debug("IPPClient initialized")

  private val killSwitch = KillSwitches.shared("printer")

  private val atomicInt = new AtomicInteger(0)

  private def getRequestId: Int =
    atomicInt.updateAndGet(
      x => if (x + 1 == Int.MaxValue) 1 else x + 1
    )

  private val ippContentType = ContentType(MediaType.customBinary("application", "ipp", NotCompressible))

  def cancelJob(jobId: Int, config: IppConfig): Future[CancelJobResponse] =
    dispatch[CancelJobResponse](CancelJob(jobId), config)

  def printJob(data: ByteString, config: IppConfig): Future[PrintJobResponse] =
    dispatch[Response.PrintJobResponse](PrintJob(data), config)

  def printerAttributes(config: IppConfig): Future[GetPrinterAttributesResponse] =
    dispatch[GetPrinterAttributesResponse](GetPrinterAttributes, config)

  def getJobAttributes[T <: IppResponse](jobId: Int, config: IppConfig): Future[GetJobAttributesResponse] =
    dispatch[GetJobAttributesResponse](GetJobAttributes(jobId), config)

  def poll(jobId: Int, config: IppConfig): Future[Response.JobData] =
    new PollingService(this, killSwitch).poll(jobId, config)

  final protected[services] def dispatch[A <: IppResponse](ev: OperationType, config: IppConfig)(
      implicit tag: TypeTag[A]
  ): Future[A] = {

    val service = new RequestService("ipp://" + config.host, queue = config.queue, requestId = getRequestId)

    val body = ev match {

      case CancelJob(jobId) =>
        Source.single(service.cancelJob(CancelJob(jobId).operationId, jobId))

      case PrintJob(data) =>
        Source.single(service.printJob(PrintJob(data).operationId, data))

      case GetPrinterAttributes =>
        Source.single(service.getPrinterAttributes(GetPrinterAttributes.operationId))

      case GetJobAttributes(jobId) =>
        println("GetJobAttributes")
        Source.single(service.getJobAttributes(GetJobAttributes(jobId).operationId, jobId))

    }

    val ntt = HttpEntity(ippContentType, body)

    val request = HttpRequest(HttpMethods.POST, uri = s"http://${config.host}:${config.port}", entity = ntt)

    val response = this.execute(request)
    val result = response.flatMap {
      case HttpResponse(StatusCodes.OK, headers, entity, _) =>
        Unmarshal(entity).to[ByteString]

      case resp => Future.failed(new Exception(s"Unexpected status code ${resp.status}"))
    }

    result.map(bs => new Response(bs).getResponse[A](ev))

  }

  def execute(request: HttpRequest): Future[HttpResponse] = http.singleRequest(request)

  def shutdown(): Future[Unit] =
    Future.successful(killSwitch.shutdown())

}
