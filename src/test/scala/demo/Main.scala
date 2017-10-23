package demo

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ ActorMaterializer, Materializer }
import akka.util.ByteString
import de.envisia.{ Constants, Response }
import de.envisia.services.IPPClient
import scala.concurrent.{ Await, ExecutionContext, Future }
import scala.concurrent.duration._

object Main {


  def main(args: Array[String]): Unit = {

    implicit val actorSystem: ActorSystem           = ActorSystem()
    implicit val mat: Materializer                  = ActorMaterializer()
    implicit val executionContext: ExecutionContext = actorSystem.dispatcher


    val client = new IPPClient("http", "192.168.179.149", Constants.WELL_KNOWN_PORT, "", Some(""), Some(""))(actorSystem, mat)

    val response = client.execute

    val result = response.flatMap {
      case HttpResponse(StatusCodes.OK, headers, entity, _) =>
        Unmarshal(entity).to[ByteString]

      case resp => Future.failed(new Exception(s"Unexpected status code ${resp.status}"))
    }

    val x = Await.result(result, 10.seconds)

    val y = new Response(x).getResponse


    Http().shutdownAllConnectionPools()
    actorSystem.terminate()

  }

}
