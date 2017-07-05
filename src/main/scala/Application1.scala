import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

/**
  * Created by sneema on 20/06/2017 AD.
  */
object Application1 {
  def main(args: Array[String]): Unit ={
    implicit val actorSystem = ActorSystem("system")
    implicit val actorMaterializer = ActorMaterializer()
    val route = {
      pathSingleSlash{
        get{
          complete{
            "two paths from here."
          }
        }
      } ~
      path("details"){
        get{
          complete{
            "d1 and d2 are here."
          }
        }
      } ~ path("details"/"d1"){
        get{
          complete{
            "d1 is here"
          }
        }
      }~ path("details"/"d2"){
        get{
          complete{
            "d2 is here"
          }
        }
      } ~
      path("customer") {
        get {
          complete {
            "HEllo World"
          }
        }
      }~
        path(IntNumber) { int =>
          complete(if (int % 2 == 0) "even ball" else "odd ball")
        }

    }
    Http().bindAndHandle(route,"localhost",8080)
    println("server started at 8080")
  }
}
