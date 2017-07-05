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
  val applicationId = "application_1496843407552_0011"
  val appAttemptId = "appattempt_1496843407552_0011_000001"
  getRCA(applicationId,appAttemptId)
  def getRCA(applicationId: String, appAttemptId: String): Unit ={
    val lines1 = Source.fromFile("/Users/sneema/Documents/detailedLogs/"+ applicationId +"/"+ appAttemptId +"/stdoutLog.txt").getLines().mkString("\n")
    val patternJ = ".+Exception[^\\n]++(\\s+at .++)+".r
    val m1 = patternJ.findAllIn(lines1).toList
    val patternP = "Traceback[^\\n]+\\s(\\s*File.*\\s+(.*\\s)?((ERROR|error|IndexError):.*\\s)*)+".r
    val m2 = patternP.findAllIn(lines1).toList
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
//        val writer = new PrintWriter("/Users/sneema/Documents/detailedLogs/"+ applicationId +"/"+ appAttemptId+"/pythonStackTrace.txt")
          writer2.write(countP.toString+"\n")
        writer2.write(e)
        countP=countP+1
        }
        //writer.close()}
    }
    writer1.close()
    writer2.close()
  }

}
