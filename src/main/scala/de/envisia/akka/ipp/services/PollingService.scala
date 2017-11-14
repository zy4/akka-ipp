package de.envisia.akka.ipp.services

import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.{Materializer, SharedKillSwitch}
import de.envisia.akka.ipp.Response.JobData

import scala.concurrent.{ExecutionContext, Future}

class PollingService(client: IPPClient, killSwitch: SharedKillSwitch)(
    implicit mat: Materializer,
    val ec: ExecutionContext
) {

  def poll(jobId: Int): Future[JobData] =
    Source.fromGraph(new JobStateSource(jobId, client)).viaMat(killSwitch.flow)(Keep.left).runWith(Sink.head)

}
