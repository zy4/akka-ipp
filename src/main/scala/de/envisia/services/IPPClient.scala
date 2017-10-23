package de.envisia.services

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.Materializer

import scala.concurrent.{ ExecutionContext, Future }

class IPPClient(host: String, port: Int, queue: String, username: Option[String], password: Option[String])(
    implicit actorSystem: ActorSystem,
    mat: Materializer
) extends HttpRequestService {

  override implicit val ec: ExecutionContext = actorSystem.dispatcher
  override def execute(req: HttpRequest): Future[HttpResponse] = Http().singleRequest(req)

}
