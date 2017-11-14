package de.envisia.akka.ipp.services

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.Materializer
import de.envisia.akka.ipp.Response.JobData
import scala.concurrent.Future

class PollingService(jobId: Int, client: IPPClient)(
    implicit actorSystem: ActorSystem,
    mat: Materializer
) {

  def poll(): Future[JobData] =
    Source.fromGraph(new JobStateSource(jobId, client)).runWith(Sink.head)

}
