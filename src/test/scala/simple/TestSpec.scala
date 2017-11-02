package simple

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import akka.http.scaladsl.testkit.ScalatestUtils
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import org.scalatest.{BeforeAndAfterAll, FreeSpec, Matchers}

class TestSpec extends FreeSpec with Matchers with BeforeAndAfterAll with ScalatestUtils {
  implicit val system       = ActorSystem(getClass.getSimpleName)
  implicit val materializer = ActorMaterializer()
  import system.dispatcher


  "The PredefinedFromEntityUnmarshallers" - {
    "stringUnmarshaller should unmarshal `text/plain` content in UTF-8 to Strings" in {
      Unmarshal(HttpEntity("Hällö")).to[String] should evaluateTo("Hällö")
    }
    "stringUnmarshaller should assume UTF-8 for textual content type with missing charset" in {
      Unmarshal(HttpEntity(MediaTypes.`text/plain`.withMissingCharset, "Hällö".getBytes("UTF-8")))
        .to[String] should evaluateTo("Hällö")
    }
    "charArrayUnmarshaller should unmarshal `text/plain` content in UTF-8 to char arrays" in {
      Unmarshal(HttpEntity("árvíztűrő ütvefúrógép")).to[Array[Char]] should evaluateTo(
        "árvíztűrő ütvefúrógép".toCharArray
      )
    }
  }

}
