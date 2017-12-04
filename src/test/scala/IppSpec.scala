package test

import java.nio.file.{Files, Paths}

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.ByteString
import de.envisia.akka.ipp.attributes.IPPValue
import de.envisia.akka.ipp.attributes.IPPValue.TextVal
import de.envisia.akka.ipp.{IPPClient, IPPConfig, Response}
import utest._

import scala.concurrent.{ExecutionContext, Future}

object IppSpec extends TestSuite {

  implicit val actorSystem: ActorSystem           = ActorSystem()
  implicit val mat: Materializer                  = ActorMaterializer()
  implicit val executionContext: ExecutionContext = actorSystem.dispatcher

  val http = Http()
  val client =
    new IPPClient(http)(mat, executionContext)
  val config = IPPConfig("localhost", port = 6632, queue = "/printers/cups-pdf")

  val pA: Future[Response.GetPrinterAttributesResponse] = client.printerAttributes(config)
  val pdf = ByteString(Files.readAllBytes(Paths.get("examples/pdf-sample.pdf")))
  val jA: Future[Response.PrintJobResponse] = client.printJob(pdf, config)


  override def tests = Tests {
    val response: Future[(Short, Map[String, List[IPPValue]])] = for {
      r <- pA
    } yield (r.version, r.attributes)
    'versionTest - {
      response.map(_._1 ==> 2)
    }
    'someAttrTest - {
      response.map(_._2("natural-language-configured").head.asInstanceOf[TextVal].value ==> "en-us")
    }
    'printJobTest - {
      val jobResponse: Future[Response.JobData] = for {
        r <- jA
      } yield r.jobData
      jobResponse.map(_.jobState ==> 3) // processing
    }
  }

  override def utestAfterAll(): Unit = {
    http.shutdownAllConnectionPools().onComplete(_ => actorSystem.terminate())
  }
}
