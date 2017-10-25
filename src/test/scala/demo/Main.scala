package demo

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, Materializer}
import de.envisia.Constants
import de.envisia.services.IPPClient

import scala.concurrent.ExecutionContext

object Main {

  def main(args: Array[String]): Unit = {

    implicit val actorSystem: ActorSystem           = ActorSystem()
    implicit val mat: Materializer                  = ActorMaterializer()
    implicit val executionContext: ExecutionContext = actorSystem.dispatcher

    val client =
      new IPPClient("http", "192.168.179.149", Constants.WELL_KNOWN_PORT, "", Some(""), Some(""))(actorSystem, mat)

    client.printJob()

    Http().shutdownAllConnectionPools()
    actorSystem.terminate()

  }

}
