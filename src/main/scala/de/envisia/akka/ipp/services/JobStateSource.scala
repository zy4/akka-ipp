package de.envisia.akka.ipp.services

import akka.event.slf4j.Logger
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

  private val logger = Logger("PollingLogger")

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new TimerGraphStageLogic(shape) {

    val callback: AsyncCallback[Try[GetJobAttributesResponse]] = getAsyncCallback[Try[GetJobAttributesResponse]] {
      case Success(value) =>
        logger.info("Success")
        if (value.jobData.jobState == 9 ||
            value.jobData.jobState == 8 ||
            value.jobData.jobState == 7 ||
            value.jobData.jobState == 6) {
          push(out, value.jobData)
          completeStage()
        } else {
          logger.info("Waiting")
          println(value.jobData.jobStateReasons)
          scheduleOnce(None, config.pollingInterval)
        }
      case Failure(t) =>
        logger.info("Failed")
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
