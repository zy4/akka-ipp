package de.envisia.akka.ipp.services

import akka.actor.ActorSystem
import akka.stream.{Attributes, Outlet, SourceShape}
import akka.stream.stage._
import de.envisia.akka.ipp.Response.{GetJobAttributesResponse, JobData}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

class JobStateSource(jobId: Int, client: IPPClient)(implicit actorSystem: ActorSystem)
    extends GraphStage[SourceShape[JobData]] {

  implicit val ec: ExecutionContext             = actorSystem.dispatcher
  private val out: Outlet[JobData]              = Outlet("JobStatusSource.out")
  override lazy val shape: SourceShape[JobData] = SourceShape.of(out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new TimerGraphStageLogic(shape) {

    val callback: AsyncCallback[Try[GetJobAttributesResponse]] = getAsyncCallback[Try[GetJobAttributesResponse]] {
      case Success(value) =>
        println("Success")
        if (value.jobData.jobState == 9 || value.jobData.jobState == 8 || value.jobData.jobState == 7) {
          push(out, value.jobData)
          completeStage()
        } else {
          println("Waiting")
          scheduleOnce(None, 500.milliseconds)
        }
      case Failure(t) =>
        println("Failure")
        fail(out, t)
    }

    setHandler(out, new OutHandler {
      override def onPull(): Unit =
        client.getJobAttributes(jobId).onComplete(callback.invoke)
    })

    override protected def onTimer(timerKey: Any): Unit =
      client.getJobAttributes(jobId).onComplete(callback.invoke)

  }
}
