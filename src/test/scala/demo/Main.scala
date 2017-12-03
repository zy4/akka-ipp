package demo

import java.nio.file.{Files, Paths}

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.scaladsl.FileIO
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.ByteString
import de.envisia.akka.ipp.{IPPClient, IPPConfig}
import de.envisia.akka.ipp.attributes.Attributes._

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

object Main {

  def main(args: Array[String]): Unit = {

    implicit val actorSystem: ActorSystem           = ActorSystem()
    implicit val mat: Materializer                  = ActorMaterializer()
    implicit val executionContext: ExecutionContext = actorSystem.dispatcher

    val http = Http()

    val client =
      new IPPClient(http)(mat, executionContext)

    val config = IPPConfig("localhost", port=6632, queue="/printers/cups-pdf")

    //val x        = ByteString(Files.readAllBytes(Paths.get("examples/pdf-sample.pdf")))
    //val printJob = client.printJob(x, config)
    //val y = Await.result(printJob, 10.seconds)

    //val x = client.poll(10, config)

    val z = client.cancelJob(10, config)

    //Await.result(z, 10.seconds)
    //val y = Await.result(x, 10.seconds)

    /* val jobs = for (i <- 1 to 2) yield {
      if (i % 10 == 0)
        Thread.sleep(200)
      client.printerAttributes(config)
    }

    Await.ready(Future.sequence(jobs), 10.minutes)
    */

    //val checkJob = client.getJobAttributes(102)
    //Await.result(checkJob, 10.seconds)

    http.shutdownAllConnectionPools().onComplete(_ => actorSystem.terminate())

  }

}
