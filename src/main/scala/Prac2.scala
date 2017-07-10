import java.io.PrintWriter

import scala.io.Source

/**
  * Created by sneema on 30/06/2017 AD.
  */
object Prac2 extends App{
 /* //val lines1 = ""
  val lines1 = Source.fromFile("/Users/sneema/Documents/detailedLogs/application_1496843407552_0013/"+"appattempt_1496843407552_0013_000001"+"/stdoutLog.txt").getLines().mkString("\n")
  //val ps2 = Json.parse(lines1)
  //println("ps2ofattempt = ",ps2 \ "trackingURL")
  println("lines11111",lines1)
  val patternJ= ".+Exception[^\\n]++(\\s+at .++)+".r
  val patternP = "Traceback[^\\n]+\\s(\\s*File.*\\s+(.*\\s)?((ERROR|error|IndexError):.*\\s)*)+".r
  //val pattern = ".+Traceback+\\s+(most recent call last):+\\s+".r
  //val pattern1  = ".+Traceback (most recent call last)(.*)".r
  //val pattern = ".+Traceback (most recent call last)[^\\n]++(\\s+at .++)+".r
  val m1 = patternJ.findAllIn(lines1).toList
  val m2 = patternP.findAllIn(lines1).toList
  println("sizeofm1",m1.size)
  println("sizeofm2",m2.size)
  m1.foreach{
    e =>
      println("**********JAVASTACKTRACE************",e)
  }
  println("CHANGE OF TYPE")
  m2.foreach{
    e =>
      println("*********PYTHONSTACKTRACE*************",e)
  }*/
  //val applicationId = "application_1496843407552_0011"
  //val appAttemptId = "appattempt_1496843407552_0011_000001"
  getRCA(applicationId,appAttemptId)
  def getRCA(applicationId: String, appAttemptId: String): Unit ={
   
   def writeIntoDB(message: String,stackTrace: String,countJts: Int): Unit ={

      val mongoClient = MongoClient()
      val db = mongoClient("yarnLogs")
      val collection = db("applicationDetails")
      val ob  = MongoDBObject("appId" -> applicationId)
      var count = countJts
      collection.find(ob).foreach{
        e =>{
          val x = e.toString
          //x=e.toString
          val jsv = Json.parse(x)
          val finishedTime = (jsv \\ "finishedTime").mkString("").replaceAll("\"","")
          val finalAppStatus = (jsv \\ "finalAppStatus").mkString("").replaceAll("\"","")
          val appType = (jsv \\"type").mkString("").replaceAll("\"","")
          val host = (jsv \\"host").mkString("").replaceAll("\"","")
          val appId = applicationId
          val attemptId = appAttemptId
          //val message = m3.mkString("\n").split("\n")(0)
          //val stackTrace = m3


          val jsobj = Json.obj(
            ("appId" -> Json.toJson(appId)),
            ("attemptId" -> Json.toJson(appAttemptId)),
            ("host" -> Json.toJson(host)),
            ("type" -> Json.toJson(appType)),
            ("message" -> Json.toJson(message)),
            ("stackTrace" -> Json.toJson(stackTrace)),
            //("fullStackTrace" -> Json.toJson(fullStackTrace)),
            ("finishedTime" -> Json.toJson(finishedTime)),
            ("finalAppStatus" -> Json.toJson(finalAppStatus))
          )
          Insert.InsertErrorDetails(jsobj)
          println(count)
          println(jsobj)
          println("\n")
        }

      }
    }
    val lines1 = Source.fromFile("/Users/sneema/Documents/detailedLogs/"+ applicationId +"/"+ appAttemptId +"/stdoutLog.txt").getLines().mkString("\n")
    val patternJ = ".+Exception[^\\n]++(\\s+at .++)+".r
    val m1 = patternJ.findAllIn(lines1).toList
    val patternP = "Traceback[^\\n]+\\s(\\s*File.*\\s+(.*\\s)?((ERROR|error|IndexError):.*\\s)*)+".r
    val m2 = patternP.findAllIn(lines1).toList
    val patternJTS = "(\\d{2}\\/\\d{2}\\/\\d{2} \\d{2}:\\d{2}:\\d{2})(.*\\n.*+(\\s+at .++)+)".r
    val m3 = patternJTS.findAllIn(lines1).toList
    val writer3 = new PrintWriter("/Users/sneema/Documents/detailedLogs/"+ applicationId +"/"+ appAttemptId+"/javaStackTraceTimeStamp.txt")
    val writer1 = new PrintWriter("/Users/sneema/Documents/detailedLogs/"+ applicationId +"/"+ appAttemptId+"/javaStackTrace.txt")
    val writer2 = new PrintWriter("/Users/sneema/Documents/detailedLogs/"+ applicationId +"/"+ appAttemptId+"/pythonStackTrace.txt")

    var countJ=1
    m1.foreach{
      e =>
        {println("***JAVASTACKTRACE***",e)
        //val writer = new PrintWriter("/Users/sneema/Documents/detailedLogs/"+ applicationId +"/"+ appAttemptId+"/javaStackTrace.txt")
       writer1.write(countJ.toString+"\n")
        writer1.write(e)
          countJ=countJ+1
        }
        //writer.close()}
    }
    var countP = 1
    m2.foreach{
      e =>
        {println("***PYTHONSTACKTRACE***",e)
//        val sp = e.split("\n")
          val message = sp(1)+sp(2)
          //println("messss = "+message)
          val stackT = e
          writeIntoDB(message,stackT,countP)

//        val writer = new PrintWriter("/Users/sneema/Documents/detailedLogs/"+ applicationId +"/"+ appAttemptId+"/pythonStackTrace.txt")
        /*writer2.write(countP.toString+"\n")
        writer2.write(e)*/
        countP=countP+1
        }
        //writer.close()}
    }
   println("size of m3 = "+m3.size)
    println("required = "+m3.mkString("\n").split("\n")(0))
    var countJTS = 1
    m3.foreach{
      e =>
        {
          println("**********JAVASTACKTRACETIMESTAMP**********",e)
          val message = e.split("\n")(0)
          val arr = e.split("\n")
          val newarr = arr.drop(1)
          val stackT = newarr.mkString("")
          //val stackT = e

          writeIntoDB(message,stackT,countJTS)
          //val writer = new PrintWriter("/Users/sneema/Documents/detailedLogs/"+ applicationId +"/"+ appAttemptId+"/javaStackTraceTimeStamp.txt")

         /* writer3.write(countJTS.toString+"\n")
          writer3.write(e)*/
          countJTS=countJTS+1
        }
    }
    writer1.close()
    writer2.close()
    writer3.close()
  }

}
