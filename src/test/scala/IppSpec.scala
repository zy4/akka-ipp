package test

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, Materializer}
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
  val response: Future[(Short, Map[String, List[IPPValue]])] = for {
    r <- pA
  } yield (r.version, r.attributes)

  Thread.sleep(2000) // TODO what is the normal way of testing futures here?

  http.shutdownAllConnectionPools().onComplete(_ => actorSystem.terminate())

  override def tests = Tests {
    'versionTest - {
      response.map(_._1 ==> 2)
    }
    'someAttrTest - {
      response.map(_._2("natural-language-configured").head.asInstanceOf[TextVal].value ==> "en-us")
    }
  }
}
