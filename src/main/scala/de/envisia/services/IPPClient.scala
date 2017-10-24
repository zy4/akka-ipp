package de.envisia.services

import java.nio.file.{ Files, Path }

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.MediaType.NotCompressible
import akka.http.scaladsl.model._
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import akka.util.ByteString
import de.envisia.Request

import scala.concurrent.{ ExecutionContext, Future }

class IPPClient(prefix: String, host: String, port: Int, queue: String, username: Option[String], password: Option[String])(
    implicit actorSystem: ActorSystem,
    mat: Materializer
) extends HttpRequestService {

  override implicit val ec: ExecutionContext = actorSystem.dispatcher

  private val ippContentType = ContentType(MediaType.customBinary("application", "ipp", NotCompressible))

  private val request = HttpRequest(HttpMethods.POST, uri = s"$prefix://$host:$port")
      .withEntity(ippContentType, new Request("ipp://" + host).printJob)

  //Source.single(attribute) + FileIO

  def printJob(file: Path) = {
    Files.size(file)
  }

  def printJob(file: Source[ByteString, Any], size: Int) = {

  }

  override def execute: Future[HttpResponse] = Http().singleRequest(request)

}
