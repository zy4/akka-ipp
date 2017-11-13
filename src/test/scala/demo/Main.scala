package demo

import java.nio.file.{Files, Paths}

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.scaladsl.FileIO
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.ByteString
import de.envisia.akka.ipp.attributes.Attributes._
import de.envisia.akka.ipp.services.IPPClient

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

object Main {

  def main(args: Array[String]): Unit = {

    implicit val actorSystem: ActorSystem           = ActorSystem()
    implicit val mat: Materializer                  = ActorMaterializer()
    implicit val executionContext: ExecutionContext = actorSystem.dispatcher

    val client =
      new IPPClient("http", "192.168.179.149", WELL_KNOWN_PORT, "print", Some(""))(actorSystem, mat)

    //val x        = ByteString(Files.readAllBytes(Paths.get("examples/pdf-sample.pdf")))
    //val printJob = client.printJob(x)
    //Await.result(printJob, 10.seconds)

     val jobs = for (i <- 1 to 2) yield {
      if (i % 10 == 0)
        Thread.sleep(200)
      client.printerAttributes()
    }

    Await.ready(Future.sequence(jobs), 10.minutes)


    //val checkJob = client.getJobAttributes(71)
    //Await.result(checkJob, 10.seconds)

    Http().shutdownAllConnectionPools()
    actorSystem.terminate()

  }

}
