package de.envisia.akka.ipp.services

import akka.stream.{Attributes, Outlet, SourceShape}
import akka.stream.stage._
import de.envisia.akka.ipp.Response.{GetJobAttributesResponse, JobData}
import de.envisia.akka.ipp.{IPPClient, IPPConfig}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

private[ipp] class JobStateSource(jobId: Int, client: IPPClient, config: IPPConfig)(implicit ec: ExecutionContext)
    extends GraphStage[SourceShape[JobData]] {

  private val out: Outlet[JobData]              = Outlet("JobStatusSource.out")
  override lazy val shape: SourceShape[JobData] = SourceShape.of(out)
  private val logger = LoggerFactory.getLogger(this.getClass)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new TimerGraphStageLogic(shape) {

    val callback: AsyncCallback[Try[GetJobAttributesResponse]] = getAsyncCallback[Try[GetJobAttributesResponse]] {
      case Success(value) =>
        logger.info("Success")
        if ((6 to 9).contains(value.jobData.jobState)) {
          push(out, value.jobData)
          completeStage()
        } else {
          logger.info("Waiting")
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
