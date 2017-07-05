import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import play.api.libs.ws.ahc.AhcWSClient

import scala.concurrent.Future

/**
  * Created by sneema on 16/06/2017 AD.
  */
object AppID extends App {

  def getAppID(rmAddress: String, serverAddress: String, parentDirectory: String): Future[Seq[String]] = {
    implicit val system = ActorSystem("system")
    implicit val actorMaterializer = ActorMaterializer()
    val wsClient = AhcWSClient()
    import scala.concurrent.ExecutionContext.Implicits.global

    //val rmAddress = "http://swapnil01-2.openstacklocal:8088"
    //val serverAddress = "http://172.22.107.151:8188"
    //println(rmAddress)


    def retrieve(ur1: String): concurrent.Future[WSResponse] = {

      wsClient.url(ur1)
        .withQueryString("some_parameter" -> "some_value", "some_other_parameter" -> "some_other_value")
        .withHeaders("Cache-Control" -> "no-cache")
        .get()
    }

    println("HELLO______HELLO")
    retrieve(rmAddress + "/ws/v1/cluster/apps").map{ wsResponse =>
      val json = Json.parse(wsResponse.body)
      //println(json)
      val applicationIDs = json \\ "id"
      //println(applicationIDs)
      applicationIDs.map(_.as[String])
//        .foreach { x =>
//        println(x.toString())
//        LogWriter.getFour(serverAddress + "/ws/v1/applicationhistory/apps", x.toString().replaceAll("\"", ""), parentDirectory)
//
//      }
    }
  }

}