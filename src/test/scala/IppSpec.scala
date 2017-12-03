package test

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, Materializer}
import de.envisia.akka.ipp.attributes.IPPValue.TextVal
import de.envisia.akka.ipp.{IPPClient, IPPConfig}
import utest._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object IppSpec extends TestSuite{


  implicit val actorSystem: ActorSystem           = ActorSystem()
  implicit val mat: Materializer                  = ActorMaterializer()
  implicit val executionContext: ExecutionContext = actorSystem.dispatcher

  val http = Http()
  val client =
    new IPPClient(http)(mat, executionContext)
  val config = IPPConfig("localhost", port=6632, queue="/printers/cups-pdf")


  var version: Int = -1
  var naturalLanguageConfigured: String = "foo"
  client.printerAttributes(config).onComplete {
    case Success(response) =>
      version = response.version.toInt
      naturalLanguageConfigured = response.attributes("natural-language-configured").head.asInstanceOf[TextVal].value
    case Failure(_) => println("FAILED")
  }

  Thread.sleep(2000)
  http.shutdownAllConnectionPools().onComplete(_ => actorSystem.terminate())

  override def tests = Tests{
    'versionTest - {
      version ==> 2
    }
    'someAttrTest - {
      naturalLanguageConfigured ==> "en-us"
    }
  }
}

