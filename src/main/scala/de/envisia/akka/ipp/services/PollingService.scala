package de.envisia.akka.ipp.services

import akka.actor.ActorSystem
import akka.stream.scaladsl.{ Sink, Source }
import akka.stream.{ Materializer, SourceShape }

import scala.concurrent.Future

class PollingService(jobId: Int, client: IPPClient)(
    implicit actorSystem: ActorSystem,
    mat: Materializer
) {


  def poll(): Future[java.io.Serializable] = {

    Source.fromGraph(new JobStateSource(jobId, client)).runWith(Sink.head)

  }

}