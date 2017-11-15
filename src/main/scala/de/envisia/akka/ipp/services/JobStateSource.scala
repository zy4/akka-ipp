package de.envisia.akka.ipp.services

import akka.stream.{ Attributes, Outlet, SourceShape }
import akka.stream.stage._
import de.envisia.akka.ipp.Response.{ GetJobAttributesResponse, JobData }
import de.envisia.akka.ipp.model.IppConfig

import scala.concurrent.ExecutionContext
import scala.util.{ Failure, Success, Try }

class JobStateSource(jobId: Int, client: IPPClient, config: IppConfig)(implicit ec: ExecutionContext)
    extends GraphStage[SourceShape[JobData]] {

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
          //value.jobData.jobStateReasons
          println("Waiting")
          println(value.jobData.jobStateReasons)
          scheduleOnce(None, config.pollingInterval)
        }
      case Failure(t) =>
        println("Failure")
        fail(out, t)
    }

    setHandler(out, new OutHandler {
      override def onPull(): Unit =
        client.getJobAttributes(jobId, config).onComplete(callback.invoke)
    })

    override protected def onTimer(timerKey: Any): Unit =
      client.getJobAttributes(jobId, config).onComplete(callback.invoke)

  }
}
