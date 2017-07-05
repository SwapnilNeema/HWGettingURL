/**
  * Created by sneema on 20/06/2017 AD.
  *
  */
object InfoInput extends App{

  //val resourceManagerAddress = scala.io.StdIn.readLine("Enter resource manager address : ")
  val resourceManagerAddress = "http://swapnil01-2.openstacklocal:8088"
  val serverAddress = "http://172.22.107.151:8188"
  val parentDirectory = "/Users/sneema/Documents/detailedLogs"
  import scala.concurrent.ExecutionContext.Implicits.global

  AppID.getAppID(resourceManagerAddress,serverAddress,parentDirectory).map{
    apps =>
      apps.foreach{
        x =>LogWriter.getFour(serverAddress + "/ws/v1/applicationhistory/apps", x.replaceAll("\"", ""), parentDirectory)
      }
  }
}
