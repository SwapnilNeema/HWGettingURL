/*
/**
  * Created by sneema on 14/06/2017 AD.
  */
class getLogs(val logURL: String) {

}
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import play.api.libs.ws.ahc.AhcWSClient

object FindLogs extends App{
  import scala.concurrent.ExecutionContext.Implicits.global


  implicit val system = ActorSystem("system")
  implicit val actorMaterializer = ActorMaterializer()
  val wsClient = AhcWSClient()

  wsClient
    .url("http://jsonplaceholder.typicode.com/comments/1")
    .withQueryString("some_parameter" -> "some_value", "some_other_parameter" -> "some_other_value")
    .withHeaders("Cache-Control" -> "no-cache")
    .get()
    .map { wsResponse =>
      if (! (200 to 299).contains(wsResponse.status)) {
        sys.error(s"Received unexpected status ${wsResponse.status} : ${wsResponse.body}")
      }
      println(s"OK, received ${wsResponse.body}")
      println(s"The response header Content-Length was ${wsResponse.header("Content-Length")}")
    }
  val source: Source[Int,NotUsed] = Source(1 to 100)
  source.runForeach(i => println(i))//(actorMaterializer)

}
*/
