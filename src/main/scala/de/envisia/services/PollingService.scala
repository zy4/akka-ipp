package de.envisia.services

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ HttpRequest, HttpResponse }
import akka.stream.Materializer
import de.envisia.Response

import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future }

class PollingService()(implicit actorSystem: ActorSystem, mat: Materializer) extends HttpRequestService {

  override implicit val ec: ExecutionContext = actorSystem.dispatcher



  def poll(jobId: Int) = {

    Future {
      mat.scheduleOnce(5.seconds, () => {


      })
    }

  }


  override def execute(request: HttpRequest): Future[HttpResponse] = Http().singleRequest(request)
}
