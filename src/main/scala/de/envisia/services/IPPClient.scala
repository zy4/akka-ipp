package de.envisia.services

import java.nio.file.{ Files, Path, Paths }

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.MediaType.NotCompressible
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import akka.stream.scaladsl.{ FileIO, Source }
import akka.util.ByteString
import de.envisia.Response
import scala.concurrent.duration._

import scala.concurrent.{ Await, ExecutionContext, Future }

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

  override implicit val ec: ExecutionContext = actorSystem.dispatcher

  private val ippContentType = ContentType(MediaType.customBinary("application", "ipp", NotCompressible))

  private val data1 = new RequestService("ipp://" + host).printJob

  private val data3 = new RequestService("ipp://" + host).getPrinterAttributes

  private val data2 = Source
    .single(data1)
    .concat(FileIO.fromPath(Paths.get("examples/pdf-sample.pdf")))

  private val entity = HttpEntity(ippContentType, data2)

  private val entity2 = HttpEntity(ippContentType, data3)

  private val request = HttpRequest(HttpMethods.POST, uri = s"$prefix://$host:$port", entity = entity)

  //Source.single(attribute) + FileIO

  def printJob(/*file: Path*/): Unit = {

    val response = this.execute

    val result = response.flatMap {
      case HttpResponse(StatusCodes.OK, headers, entity, _) =>
        Unmarshal(entity).to[ByteString]

      case resp => Future.failed(new Exception(s"Unexpected status code ${resp.status}"))
    }

    val x = Await.result(result, 10.seconds)

    val ippResponse = new Response(x).getResponse

  }


  def validateJob() = {


  }

  def getStatus() = {


  }


  //def printJob(file: Source[ByteString, Any]) = {}

  override def execute: Future[HttpResponse] = Http().singleRequest(request)

}
