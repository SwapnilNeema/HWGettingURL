

import java.io.{File, PrintWriter}

import akka.actor._
import akka.stream._
import org.jsoup.Jsoup
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSResponse
import play.api.libs.ws.ahc.AhcWSClient

import scala.io.Source
/**
  * Created by sneema on 15/06/2017 AD.
  */
object LogWriter extends App {
  def getFour(serverAddress: String, applicationId: String, parentDirectory: String): Unit ={

    //val parentDirectory = "/Users/sneema/Documents/detailedLogs"
    val applicationDirectory = parentDirectory+"/"+applicationId
    val appIDdir =new File(applicationDirectory)
    val check1 = appIDdir.mkdir()
    if(check1)println("**************************************made the directory "+applicationId+" **************************************")
    else println("**************************************failed to create directory "+applicationId+" **************************************!!")

     import scala.concurrent.ExecutionContext.Implicits.global

    implicit val system = ActorSystem("system")
    implicit val actorMaterializer = ActorMaterializer()
    val wsClient = AhcWSClient()
    def retrieve(ur1: String): concurrent.Future[WSResponse] = {
      wsClient.url(ur1)
        .withQueryString("some_parameter" -> "some_value", "some_other_parameter" -> "some_other_value")
        .withHeaders("Cache-Control" -> "no-cache")
        .get()
    }
    def writeStatus(appAttemptIDdir: File,appAttempt: String): Unit ={
      retrieve(serverAddress+"/"+applicationId+"/appattempts/"+appAttempt).map{ wsResponse =>
        val appAttemptDetails = Json.parse(wsResponse.body)
        val startedTime = (appAttemptDetails \\ "startedTime").mkString("")
        val finishedTime = (appAttemptDetails \\ "finishedTime").mkString("")
        val diagnosticsInfo = (appAttemptDetails \\ "diagnosticsInfo").mkString("").replaceAll("\"","")
        val trackingURL = (appAttemptDetails \\ "originalTrackingUrl").mkString("").replaceAll("\"","")
        val appAttemptState = (appAttemptDetails \\"appAttemptState").mkString("").replaceAll("\"","")
        val host = (appAttemptDetails \\ "host").mkString("").replaceAll("\"","")
        val aId = applicationId
        val cId = (appAttemptDetails \\ "amContainerId").mkString("").replaceAll("\"","")
        val aaId = (appAttemptDetails \\ "appAttemptId").mkString("").replaceAll("\"","")
        // print("status and details of application-----------------------*_*_*_*_*_*__*_**_**_*_*_*_*_*_")
        // println("ye lo = \n "+startedTime+" \n "+finishedTime+" \n "+submittedTime+" \n"+finalAppStatus)

        val jsobj = Json.obj(
          ("appId" -> Json.toJson(aId)),
          ("appAttemptId" -> Json.toJson(aaId)),
          ("startedTime" -> Json.toJson(startedTime)),
          ("finishedTime" -> Json.toJson(finishedTime)),
          ("diagnosticsInfo" -> Json.toJson(diagnosticsInfo)),
          ("trackingURL" -> Json.toJson(trackingURL)),
          ("appAttemptState" -> Json.toJson(appAttemptState)),
          ("host" -> Json.toJson(host)),
          ("amcontainerId" -> Json.toJson(cId))
        )

        //println("valofobj = ",jsobj)
        val writer = new PrintWriter(applicationDirectory +"/"+appAttempt+"/appAttemptDetails.json")
        writer.write(jsobj.toString())
        writer.close()
        val lines = Source.fromFile(applicationDirectory+"/"+appAttempt+"/appAttemptDetails.json").getLines().mkString
        val ps2 = Json.parse(lines)
       // println("ps2ofattempt = ",ps2 \ "trackingURL")

        Insert.InsertDBAppAttempt(jsobj)
 }}

    def getLogs(logurl: String, appAttempt: String): Unit ={
      //println(appAttempt)
      //val appAttemptDir = applicationDirectory+"/"+appAttempt
      val appAttemptIDdir =new File(applicationDirectory+"/"+appAttempt)
      val check1 = appAttemptIDdir.mkdir()
      println(appAttemptIDdir)


      if(check1)println("**************************************made the attempt directory "+appAttempt+" **************************************")
      else println("**************************************failed to create the directory"+appAttempt+" **************************************!!")

      writeStatus(appAttemptIDdir,appAttempt)

      val stderr = logurl+"/stderr/?start=0"
      val stdout = logurl+"/stdout/?start=0"
      val directoryInfo = logurl+"/directory.info/?start=0"
      val containerLaunch = logurl+"/launch_container.sh/?start=0"
      retrieve(containerLaunch).map{wsResponse => println()
        //println(wsResponse.body)
        val doc1 = Jsoup.parse(wsResponse.body)
        val logsOnly = doc1.select("pre").text()
        //println("------------------containerLaunch---------","\n",logsOnly)
        //val file = new File(appAttemptIDdir+"/launchContainerLog.txt")
        /*val file = new File("/Users/sneema/Documents/hello.txt")
        file.createNewFile()*/
        val writer = new PrintWriter(appAttemptIDdir+"/launchContainerLog.txt")
        writer.write(logsOnly)
        writer.close()
      }
      retrieve(directoryInfo).map{wsResponse =>
        //println(wsResponse.body)
        val doc1 = Jsoup.parse(wsResponse.body)
        val logsOnly = doc1.select("pre").text()
        //println("------------------directoryInfo logs---------","\n",logsOnly)
        val writer = new PrintWriter(appAttemptIDdir+"/directoryInfoLog.txt")
        writer.write(logsOnly)
        writer.close()
      }
      retrieve(stdout).map{wsResponse =>
        //println(wsResponse.body)
        val doc1 = Jsoup.parse(wsResponse.body)
        val logsOnly = doc1.select("pre").text()
        //println("------------------stdout logs---------","\n",logsOnly)
        val writer = new PrintWriter(appAttemptIDdir+"/stdoutLog.txt")
        writer.write(logsOnly)
        writer.close()
      }
      retrieve(stderr).map{wsResponse => println()
        //println(wsResponse.body)
        val doc1 = Jsoup.parse(wsResponse.body)
        val logsOnly = doc1.select("pre").text()
        //println("------------------stderr logs---------","\n",logsOnly)
        val writer = new PrintWriter(appAttemptIDdir+"/stderrLog.txt")
        writer.write(logsOnly)
        writer.close()
      }

    }

    val appURL = serverAddress+"/"+applicationId
    retrieve(appURL).map{ wsResponse =>
      val appDetails = Json.parse(wsResponse.body)
      val aID = (appDetails \\ "appId").mkString("").replaceAll("\"","")
      val startedTime = (appDetails \\ "startedTime").mkString("")
      val finishedTime = (appDetails \\ "finishedTime").mkString("")
      val submittedTime = (appDetails \\ "submittedTime").mkString("")
      val finalAppStatus = (appDetails \\"finalAppStatus").mkString("").replaceAll("\"","")
      val host = (appDetails \\ "host").mkString("").replaceAll("\"","")
      val appType = (appDetails \\ "type").mkString("").replaceAll("\"","")
      val elTime = (appDetails \\ "elapsedTime").mkString("")
      val queue = (appDetails \\ "queue").mkString("").replaceAll("\"","")
      val name = (appDetails \\ "name").mkString("").replaceAll("\"","")
      val dinfo = (appDetails \\ "diagnosticsInfo").mkString("").replaceAll("\"","")
      val jsobj = Json.obj(
        ("appId" -> Json.toJson(aID)),
        ("startedTime" -> Json.toJson(startedTime)),
        ("finishedTime" -> Json.toJson(finishedTime)),
        ("submittedTime" -> Json.toJson(submittedTime)),
        ("finalAppStatus" -> Json.toJson(finalAppStatus)),
        ("host" -> Json.toJson(host)),
        ("type" -> Json.toJson(appType)),
        ("elapsedTime" -> Json.toJson(elTime)),
        ("queue" -> Json.toJson(queue)),
        ("name" -> Json.toJson(name)),
        ("diagnosticsInfo" -> Json.toJson(dinfo))
      )

      //println("valofobj = ",jsobj)
      val writer = new PrintWriter(applicationDirectory +"/applicationDetails.json")
      writer.write(jsobj.toString())
      writer.close()
/*
      val newObj = MongoDBObject(jsobj.toString())
      def saveObject(obj: DBObject ): Unit ={
        MongoFactoryA.collection.save(obj)
      }
      saveObject(newObj)*/

      Insert.InsertDBApp(jsobj)

    }
    val aurl = serverAddress+"/"+applicationId
    val appIdURL = serverAddress+"/"+applicationId+"/appattempts"
    retrieve(appIdURL)
      .map { wsResponse =>
        val json = Json.parse(wsResponse.body)

        //println(json)
        val appAttempts = json \\ "appAttemptId"
        val numOfAttempts = appAttempts.size    // number of attempts in this application.....----...--..-l-.-.-l.-.-.-.
        var failedAttempts = 0
        val attState = json \\ "appAttemptState"
        attState.foreach{
          x => if(x.toString().replaceAll("\"","") == "FAILED") failedAttempts=failedAttempts+1
        }
        retrieve(aurl).map{
          wsResponse =>
            val js1 = Json.parse(wsResponse.body)
            val appHost = (js1 \\ "host").mkString("").replaceAll("\"","")
            val appElapsedTime = (js1 \\ "elapsedTime").mkString("").replaceAll("\"","")
            val lastAttempt = js1 \\ "currentAppAttemptId"
            lastAttempt.foreach{
              x =>
                val laurl = appIdURL+"/"+x.toString().replaceAll("\"","")
                retrieve(laurl).map{
                  wsResponse =>
                    val js2 = Json.parse(wsResponse.body)
                    writeAppState(applicationId,numOfAttempts,failedAttempts,appHost,appElapsedTime,js2)
                }
            }
        }
        //println("application attempt ids = ",appAttempts)
        appAttempts.foreach( x => {
          val attemptURL = x.toString().replaceAll("\"","")
          val goto = appIdURL+"/"+attemptURL+"/containers"
         // println(goto)
          retrieve(goto)
            .map{ wsResponse =>
              val js = Json.parse(wsResponse.body)
              //println("appattemps containers = ",js)
              val containerIds = js \\ "containerId"
              val containerState = js \\ "containerState"
              val containerElapsedTime = js \\ "elapsedTime"
              var totalContainerTime =0.0
              var maxContainerTime =0.0
              containerElapsedTime.foreach{
                s => if(s.as[Double]> maxContainerTime )
                      maxContainerTime= s.as[Double]
                  totalContainerTime = totalContainerTime + s.as[Double]
              }
              //println(totalContainerTime)
              val avgContainerTime = totalContainerTime / (containerElapsedTime.size)
              //println(avgContainerTime)
              var failedContainerCount =0;
              var failedNodeList = List[String]()
              containerState.foreach{
                s => println(s)
                  if(s.toString().replaceAll("\"","") != "COMPLETE")
                    {
                      failedContainerCount=failedContainerCount+1
                      val failedNode = js \\ "nodeId"
                      //failedNodeList = failedNodeList+ List(failedNode.toString())
                      failedNode.foreach{
                        q => failedNodeList = q.toString().replaceAll("\"","") :: failedNodeList
                      }
                    }
              }
              //println(containerIds.size)
              //println(containerIds)
              val numOfCont = containerIds.size

              containerIds.foreach( l =>{
                val containerURL = l.toString().replaceAll("\"","")
                writeAttemptState(numOfCont,failedContainerCount,avgContainerTime,maxContainerTime,failedNodeList,containerURL)

                val finalgo = goto+"/"+containerURL   // go to container
                //println("testkarle",containerURL,"+++++",goto)
                //println(finalgo)
                getContainer(finalgo,x.toString())
                retrieve(finalgo)
                  .map{
                    wsResponse =>
                      val js1 = Json.parse(wsResponse.body)
                      //println("inside container = ",js1)
                      val logURL = js1 \\ "logUrl"
                      //println("url = ",logURL)
                      logURL.foreach( p =>{
                        getLogs(p.toString().replaceAll("\"",""),attemptURL)
                      })
                      //Prac2.getRCA(applicationId,attemptURL)
                  }
              })
            }
        }
        )
      }
    def writeAttemptState(numofContainer: Int, failedContainerCount: Int,avgContainerTime: Double,maxContainerTime: Double, failedNodeList: List[String],containerURL: String): Unit ={
      val jsobj = Json.obj(
        ("containerId" -> Json.toJson(containerURL)),
        ("numofContainer" -> Json.toJson(numofContainer)),
        ("failedContainerCount" -> Json.toJson(failedContainerCount)),
        ("avgContainerTime" -> Json.toJson(avgContainerTime)),
        ("maxContainerTime" -> Json.toJson(maxContainerTime)),
        ("failedNodeList" -> Json.toJson(failedNodeList))
      )
      //println("js1111",jsobj)
      Insert.InsertAppAttemptState(jsobj)
    }
    def writeAppState(applicationId: String, numOfAttempts: Int, numOfFailedAttempts: Int,host: String, elapsedTime: String,lasAttSum: JsValue): Unit ={
      val jsobj = Json.obj(
        ("applicationId" -> Json.toJson(applicationId)),
        ("numOfAttempts" -> Json.toJson(numOfAttempts)),
        ("numOfFailedAttempts" -> Json.toJson(numOfFailedAttempts)),
        ("host" -> Json.toJson(host)),
        ("elapsedTime" -> Json.toJson(elapsedTime)),
        ("lastAttemptSummary" -> Json.toJson(lasAttSum))
      )
      println(jsobj)
      Insert.InsertAppAState(jsobj)
    }
    def getContainer(finalgo: String,appAttemptID: String): Unit ={
      retrieve(finalgo).map{ wsResponse =>
        val containerDetails = Json.parse(wsResponse.body)
        val assignedNodeId = (containerDetails \\"assignedNodeId").mkString("").replaceAll("\"","")
        val finishedTime = (containerDetails \\"finishedTime").mkString("").replaceAll("\"","")
        val startedTime = (containerDetails \\"startedTime").mkString("").replaceAll("\"","")
        val elapsedTime = (containerDetails \\"elapsedTime").mkString("").replaceAll("\"","")
        val diagnosticsInfo = (containerDetails \\"diagnosticsInfo").mkString("").replaceAll("\"","")
        val containerState = (containerDetails \\"containerState").mkString("").replaceAll("\"","")
        val nodeHttpAddress = (containerDetails \\"nodeHttpAddress").mkString("").replaceAll("\"","")
        val containerId = (containerDetails \\"containerId").mkString("").replaceAll("\"","")
        val appAtId = appAttemptID.replaceAll("\"","")
        val aID = applicationId
        val jsobj = Json.obj(
          ("appId" -> Json.toJson(aID)),
          ("appAttemptID" -> Json.toJson(appAtId)),
          ("containerId" -> Json.toJson(containerId)),
          ("startedTime" -> Json.toJson(startedTime)),
          ("finishedTime" -> Json.toJson(finishedTime)),
          ("elapsedTime" -> Json.toJson(elapsedTime)),
          ("containerState" -> Json.toJson(containerState)),
          ("nodeHttpAddress" -> Json.toJson(nodeHttpAddress)),
          ("assignedNodeId" -> Json.toJson(assignedNodeId)),
          ("diagnosticsInfo" -> Json.toJson(diagnosticsInfo))
        )
        Insert.InsertDBContainer(jsobj)
      }
    }

  }
}




