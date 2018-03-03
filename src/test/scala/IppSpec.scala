package test

import java.io.FileInputStream
import java.nio.file.{Files, Paths}

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.ByteString
import de.envisia.akka.ipp.attributes.IPPValue
import de.envisia.akka.ipp.attributes.IPPValue.TextVal
import de.envisia.akka.ipp.{IPPClient, IPPConfig, Response}
import utest._
import org.apache.tika.parser.pdf._
import org.apache.tika.metadata._
import org.apache.tika.parser._
import org.apache.tika.sax.BodyContentHandler

import scala.concurrent.duration.Duration
import sys.process._
import scala.concurrent.{Await, ExecutionContext, Future}

object IppSpec extends TestSuite {

  implicit val actorSystem: ActorSystem           = ActorSystem()
  implicit val mat: Materializer                  = ActorMaterializer()
  implicit val executionContext: ExecutionContext = actorSystem.dispatcher

  val http = Http()
  val client =
    new IPPClient(http)(mat, executionContext)
  val config = IPPConfig("localhost", port = 6632, queue = "/printers/cups-pdf")

  val pA: Future[Response.GetPrinterAttributesResponse] = client.printerAttributes(config)
  val pdf                                               = ByteString(Files.readAllBytes(Paths.get("examples/pdf-sample.pdf")))
  val jA: Future[Response.PrintJobResponse]             = client.printJob(pdf, config)

  val pdfParser: PDFParser = new PDFParser

  override def tests = Tests {
    val response: Future[(Short, Map[String, List[IPPValue]])] = for {
      r <- pA
    } yield (r.version, r.attributes)
    val jobResponse: Future[Response.JobData] = for {
      r <- jA
    } yield r.jobData
    'versionTest - {
      response.map(_._1 ==> 2)
    }
    'someAttrTest - {
      response.map(_._2("natural-language-configured").head.asInstanceOf[TextVal].value ==> "en-us")
    }
    'printJobTest - {
      jobResponse.map(_.jobState ==> 3) // processing
    }
    'comparePDFs - {
      val jobId   = Await.result(jobResponse, Duration.Inf).jobID
      val jobDone = Await.result(client.poll(jobId, config), Duration.Inf)
      if (jobDone.jobState == 9) {
        s"docker cp cups:/var/spool/cups-pdf/ANONYMOUS/ault__0-job_${jobDone.jobID}.pdf /tmp".!

        val stream                      = new FileInputStream(s"/tmp/ault__0-job_${jobDone.jobID}.pdf")
        val metadata: Metadata          = new Metadata
        val context: ParseContext       = new ParseContext
        val handler: BodyContentHandler = new BodyContentHandler
        pdfParser.parse(stream, handler, metadata, context)
        val content = handler.toString
        stream.close()

        val streamLocal  = new FileInputStream("examples/pdf-sample.pdf")
        val mdLocal      = new Metadata
        val contextLocal = new ParseContext
        val handlerLocal = new BodyContentHandler
        pdfParser.parse(streamLocal, handlerLocal, mdLocal, contextLocal)
        val contentLocal = handlerLocal.toString
        streamLocal.close()
        val _ = s"rm /tmp/ault__0-job_${jobDone.jobID}.pdf".!
        content ==> contentLocal
      } else {
        assert(false)
      }
    }
  }

  override def utestAfterAll(): Unit =
    http.shutdownAllConnectionPools().onComplete(_ => actorSystem.terminate())
}
