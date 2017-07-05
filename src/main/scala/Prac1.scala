import java.io.{File, PrintWriter}

import akka.actor._
import akka.stream._
import org.jsoup.Jsoup
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import play.api.libs.ws.ahc.AhcWSClient

import scala.io.Source

object Prac1 extends App{
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
  val appIDdir =new File("/Users/sneema/Documents/detailedLogs/"+"application_1496843407552_0013")
  val check1 = appIDdir.mkdir()

  if(check1)println("**************************************made the directory**************************************")
  else println("**************************************failed**************************************!!")

  def writeStatus(appAttemptIDdir: File,appAttempt: String): Unit ={
    retrieve("http://172.22.107.151:8188/ws/v1/applicationhistory/apps/application_1496843407552_0013/appattempts/"+appAttempt).map{ wsResponse =>
      val appAttemptDetails = Json.parse(wsResponse.body)
      val startedTime = (appAttemptDetails \\ "startedTime").mkString("")
      val finishedTime = (appAttemptDetails \\ "finishedTime").mkString("")
      val diagnosticsInfo = (appAttemptDetails \\ "diagnosticsInfo").mkString("")
      val trackingURL = (appAttemptDetails \\ "originalTrackingUrl").mkString("")
      val appAttemptState = (appAttemptDetails \\"appAttemptState").mkString("").replaceAll("\"","")
     // print("status and details of application-----------------------*_*_*_*_*_*__*_**_**_*_*_*_*_*_")
     // println("ye lo = \n "+startedTime+" \n "+finishedTime+" \n "+submittedTime+" \n"+finalAppStatus)

      val jsobj = Json.obj(
        ("startedTime" -> Json.toJson(startedTime)),
        ("finishedTime" -> Json.toJson(finishedTime)),
        ("diagnosticsInfo" -> Json.toJson(diagnosticsInfo)),
        ("trackingURL" -> Json.toJson(trackingURL)),
        ("appAttemptState" -> Json.toJson(appAttemptState)))

      //println("valofobj = ",jsobj)
      val writer = new PrintWriter("/Users/sneema/Documents/detailedLogs/"+"application_1496843407552_0013/"+appAttempt+"/appAttemptDetails.json")
      writer.write(jsobj.toString())
      writer.close()
      val lines = Source.fromFile("/Users/sneema/Documents/detailedLogs/application_1496843407552_0013/"+appAttempt+"/appAttemptDetails.json").getLines().mkString
      val ps2 = Json.parse(lines)
      //println("ps2ofattempt = ",ps2 \ "trackingURL")

      /*val regex = ".+Exception[^\\n]+\\n(\\t+\\Qat \\E.+\\s+)+"
      val p = Pattern.compile(regex)
      val matcher =  p.matcher(lines)
      if(matcher.find){
        println("yelomatcher:",matcher.toString)                  // regex matching using java.util.regex.pattern
      }
      else{
        println("nahimila",matcher.toString)
      }*/
      println("lines",lines)
      val pattern = "^.+Exception[^\\n]++(\\s+at .++)+".r
      val address = "123 Main Street Suite Exception 101"
      val m1 = pattern.findAllIn(lines)
      m1.foreach{
        e => println(s"Found a match: $e")
      }



  }}


  def getLogs(logurl: String, appAttempt: String): Unit ={
    println(appAttempt)
    val appAttemptIDdir =new File("/Users/sneema/Documents/detailedLogs/"+"application_1496843407552_0013"+"/"+appAttempt)
    val check1 = appAttemptIDdir.mkdir()
    println(appAttemptIDdir)

  writeStatus(appAttemptIDdir,appAttempt)


    if(check1)println("**************************************made the attempt directory**************************************")
    else println("**************************************failed**************************************!!")
    val stderr = logurl+"/stderr/?start=0"
    val stdout = logurl+"/stdout/?start=0"
    val directoryInfo = logurl+"/directory.info/?start=0"
    val containerLaunch = logurl+"/launch_container.sh/?start=0"
    retrieve(containerLaunch).map{wsResponse => println()
      //println(wsResponse.body)
      val doc1 = Jsoup.parse(wsResponse.body)
      val logsOnly = doc1.select("pre").text()
      println("------------------containerLaunch---------","\n",logsOnly)
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
      println("------------------directoryInfo logs---------","\n",logsOnly)
      val writer = new PrintWriter(appAttemptIDdir+"/directoryInfoLog.txt")
      writer.write(logsOnly)
      writer.close()
    }
    retrieve(stdout).map{wsResponse =>
      //println(wsResponse.body)
      val doc1 = Jsoup.parse(wsResponse.body)
      val logsOnly = doc1.select("pre").text()
      println("------------------stdout logs---------","\n",logsOnly)
      val writer = new PrintWriter(appAttemptIDdir+"/stdoutLog.txt")
      writer.write(logsOnly)
      writer.close()
    }
    retrieve(stderr).map{wsResponse => println()
      //println(wsResponse.body)
      val doc1 = Jsoup.parse(wsResponse.body)
      val logsOnly = doc1.select("pre").text()
      println("------------------stderr logs---------","\n",logsOnly)
      val writer = new PrintWriter(appAttemptIDdir+"/stderrLog.txt")
      writer.write(logsOnly)
      writer.close()
    }

  }

  /*class AppDetails(json: String) {

    parse1(json)

    def parse1 (json: String): String ={
      val appDetails = Json.parse(json)
      val startedTime = (appDetails \\ "startedTime").mkString("")
      val finishedTime = (appDetails \\ "finishedTime").mkString("")
      val submittedTime = (appDetails \\ "submittedTime").mkString("")
      val finalAppStatus = (appDetails \\"finalAppStatus").mkString("")



      return new Gson().toJson(this);
    }
  }*/
  retrieve("http://172.22.107.151:8188/ws/v1/applicationhistory/apps/application_1496843407552_0013").map{ wsResponse =>
    val appDetails = Json.parse(wsResponse.body)
    val startedTime = (appDetails \\ "startedTime").mkString("")
    val finishedTime = (appDetails \\ "finishedTime").mkString("")
    val submittedTime = (appDetails \\ "submittedTime").mkString("")
    val finalAppStatus = (appDetails \\"finalAppStatus").mkString("").replaceAll("\"","")
    print("status and details of application-----------------------*_*_*_*_*_*__*_**_**_*_*_*_*_*_")
    println("ye lo = \n "+startedTime+" \n "+finishedTime+" \n "+submittedTime+" \n"+finalAppStatus)

    //val stringJson = "{"+"startedTime : "+startedTime+", finishedTime : "+finishedTime+", submittedTime : "+submittedTime+", finalAppStatus : "+finalAppStatus+"}"
    //val rawJson = new Gson().toJson(stringJson)
    //val r1 = Json.toJson(stringJson)

    //println(rawJson)
    //println(r1.toString())

    val jsobj = Json.obj(
                    ("startedTime" -> Json.toJson(startedTime)),
                    ("finishedTime" -> Json.toJson(finishedTime)),
                    ("submittedTime" -> Json.toJson(submittedTime)),
                    ("finalAppStatus" -> Json.toJson(finalAppStatus)))

      println("valofobj = ",jsobj)

    //println(pretty(render(r2)))
    //println(pretty)
    //println()

    val writer = new PrintWriter("/Users/sneema/Documents/detailedLogs/"+"application_1496843407552_0013"+"/applicationDetails.json")
    writer.write(jsobj.toString())
    writer.close()
    //val appDetails = new AppDetails(wsResponse.body);
   // println("lets go "+appDetails)

    val lines = Source.fromFile("/Users/sneema/Documents/detailedLogs/application_1496843407552_0013/applicationDetails.json").getLines().mkString
    val ps2 = Json.parse(lines)
    println("ps2 = ",ps2 \ "startedTime")




  }
    retrieve("http://172.22.107.151:8188/ws/v1/applicationhistory/apps/application_1496843407552_0013/appattempts")
      .map { wsResponse =>
      val json = Json.parse(wsResponse.body)
      println(json)
      val appAttempts = json \\ "appAttemptId"
      println("appllication attempt ids = ",appAttempts)

      val parentURL = "http://172.22.107.151:8188/ws/v1/applicationhistory/apps/application_1496843407552_0013/appattempts/"
      println(parentURL)
      appAttempts.foreach( x => {
        val attemptURL = x.toString().replaceAll("\"","")
        val goto = parentURL+attemptURL+"/containers"
        //println(goto)
        retrieve(goto)
          .map{ wsResponse =>
            val js = Json.parse(wsResponse.body)
          //  println("appattemps containers = ",js)
            val containerIds = js \\ "containerId"

            //println(containerIds)
            containerIds.foreach( l =>{
              val containerURL = l.toString().replaceAll("\"","")
              val finalgo = goto+"/"+containerURL
            //  println(finalgo)
              retrieve(finalgo)
                .map{
                  wsResponse =>
                    val js1 = Json.parse(wsResponse.body)
              //      println("inside container = ",js1)
                    val logURL = js1 \\ "logUrl"
                //    println("url = ",logURL)
                    logURL.foreach( p =>{
                      /*println("this is url = ",p.toString().replaceAll("\"",""))
                     retrieve(p.toString().replaceAll("\"","")).map{
                       wsResponse =>
                         println("*****************")
                         println(wsResponse.body)
                         }
                         */
                      getLogs(p.toString().replaceAll("\"",""),attemptURL)
                     // Prac2.getRCA(applicationId,attemptURL)
                    })

                }
            })
          }

      }
      )
      println(s"OK, received ${wsResponse.body}")
      println(s"The response header Content-Length was ${wsResponse.header("Content-Length")}")
    }
  val lines1 = Source.fromFile("/Users/sneema/Documents/detailedLogs/application_1496843407552_0013/"+"appattempt_1496843407552_0013_000001"+"/stdoutLog.txt").getLines().mkString("\n")

  //val lines1 = "py4j.protocol.Py4JJavaError: An error occurred while calling o54.saveAsTextFile.: org.apache.hadoop.mapred.FileAlreadyExistsException: Output directory hdfs://swapnil01-1.openstacklocal:8020/user/root/outputcsvdetection already exists\n\tat org.apache.hadoop.mapred.FileOutputFormat.checkOutputSpecs(FileOutputFormat.java:131)\n\tat org.apache.spark.rdd.PairRDDFunctions$$anonfun$saveAsHadoopDataset$1.apply$mcV$sp(PairRDDFunctions.scala:1177)\n\tat org.apache.spark.rdd.PairRDDFunctions$$anonfun$saveAsHadoopDataset$1.apply(PairRDDFunctions.scala:1154)\n\tat org.apache.spark.rdd.PairRDDFunctions$$anonfun$saveAsHadoopDataset$1.apply(PairRDDFunctions.scala:1154)\n\tat org.apache.spark.rdd.RDDOperationScope$.withScope(RDDOperationScope.scala:150)\n\tat org.apache.spark.rdd.RDDOperationScope$.withScope(RDDOperationScope.scala:111)\n\tat org.apache.spark.rdd.RDD.withScope(RDD.scala:323)\n\tat org.apache.spark.rdd.PairRDDFunctions.saveAsHadoopDataset(PairRDDFunctions.scala:1154)\n\tat org.apache.spark.rdd.PairRDDFunctions$$anonfun$saveAsHadoopFile$4.apply$mcV$sp(PairRDDFunctions.scala:1060)\n\tat org.apache.spark.rdd.PairRDDFunctions$$anonfun$saveAsHadoopFile$4.apply(PairRDDFunctions.scala:1026)\n\tat org.apache.spark.rdd.PairRDDFunctions$$anonfun$saveAsHadoopFile$4.apply(PairRDDFunctions.scala:1026)\n\tat org.apache.spark.rdd.RDDOperationScope$.withScope(RDDOperationScope.scala:150)\n\tat org.apache.spark.rdd.RDDOperationScope$.withScope(RDDOperationScope.scala:111)\n\tat org.apache.spark.rdd.RDD.withScope(RDD.scala:323)\n\tat org.apache.spark.rdd.PairRDDFunctions.saveAsHadoopFile(PairRDDFunctions.scala:1026)\n\tat org.apache.spark.rdd.PairRDDFunctions$$anonfun$saveAsHadoopFile$1.apply$mcV$sp(PairRDDFunctions.scala:952)\n\tat org.apache.spark.rdd.PairRDDFunctions$$anonfun$saveAsHadoopFile$1.apply(PairRDDFunctions.scala:952)\n\tat org.apache.spark.rdd.PairRDDFunctions$$anonfun$saveAsHadoopFile$1.apply(PairRDDFunctions.scala:952)"
  //val ps2 = Json.parse(lines1)
  //println("ps2ofattempt = ",ps2 \ "trackingURL")
  //println("lines11111",lines1)
  val pattern = ".+Exception[^\\n]++(\\s+at .++)+".r
  val m1 = pattern.findAllIn(lines1).toList
  println("sizeofm1",m1.size)
  m1.foreach{
    e => println(s"Found a perfect match: $e")
      println("**********************",e)
  }


}
