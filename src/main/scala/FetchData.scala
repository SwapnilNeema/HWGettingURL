import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId
import play.api.libs.json.Json


/**
  * Created by sneema on 27/06/2017 AD.
  */
object FetchData extends App {
  val mongoClient = MongoClient()
  val db = mongoClient("yarnLogs")
  val collection = db("applicationDetails")
  collection.findOne() match {
    case Some(mongoDBObject) => println(mongoDBObject)
    case None => println("did not find anything.")
  }
  private val value =  new ObjectId("5950d8f0afe72baabb08b37f")

  collection.findOneByID(value) match {
    case Some(mongoDBObject) => println(mongoDBObject)
    case None => println("did not find anything.")
  }

  val o  = MongoDBObject("name" -> "HIVE-684ca61f-7d97-4ada-b6f2-e61de33aa474")

  collection.findOne(o) match {
    case Some(mongoDBObject) => println(mongoDBObject)
    case None => println("did not find anything.")
  }
  //val ad = collection.find({},{"name": 1})
 // println("ad",ad)
  println(collection.count(o))

  val distinctDocuments = collection.distinct("name")
  println(distinctDocuments)
  println(distinctDocuments.size)
  distinctDocuments.foreach{
    x => {
      val p = x.toString
     // println(p)
      val obj = MongoDBObject("name"->p)
      var maxElapsedTime=0.0
      var maxPassElapsedTime=0.0
      var maxFailElapsedTime=0.0
      var avgElapsedTime=0.0
      var avgFailElapsedTime=0.0
      var avgPassElapsedTime=0.0
      var name = p
      var passCount =0
      var failCount =0
      var failedNodeList = List[String]()
      val perNameCount = collection.find(obj).size
      //println("size is",count)
      collection.find(obj).foreach { l =>
        println(l)
        val jsobj = Json.parse(l.toString)
        val elapsedTime = (jsobj \\ "elapsedTime").mkString("").replaceAll("\"","").toDouble
        if(elapsedTime>maxElapsedTime)maxElapsedTime=elapsedTime
        avgElapsedTime = avgElapsedTime+elapsedTime
        val status = (jsobj \\ "finalAppStatus").mkString("").replaceAll("\"","")
        if(status == "SUCCEEDED"){
          passCount=passCount+1
          if(elapsedTime>maxPassElapsedTime)maxPassElapsedTime=elapsedTime
          avgPassElapsedTime=avgPassElapsedTime+elapsedTime
        }
        else if(status == "FAILED"){
          failCount=failCount+1
          if(elapsedTime>maxFailElapsedTime)maxFailElapsedTime=elapsedTime
          avgFailElapsedTime=avgFailElapsedTime+elapsedTime
          val failedNode = (jsobj \\ "host").mkString("").replaceAll("\"","")
          failedNodeList = failedNode :: failedNodeList
          //println("failed",failedNode)
        }
      }
      if(passCount>0)avgPassElapsedTime=avgPassElapsedTime/passCount
      if(failCount>0)avgFailElapsedTime=avgFailElapsedTime/failCount
      avgElapsedTime=avgElapsedTime/perNameCount
      println("name",name,"avgelaptime",avgElapsedTime,"pC",passCount,"fC",failCount,"fnl",failedNodeList,"aft",avgFailElapsedTime,"apt",avgPassElapsedTime,"mpt",maxPassElapsedTime,"mft",maxFailElapsedTime)
      val jsobj = Json.obj(
        ("name" -> Json.toJson(name)),
        ("maxElapsedTime" -> Json.toJson(maxElapsedTime)),
        ("avgElapsedTime" -> Json.toJson(avgElapsedTime)),
        ("passCount" -> Json.toJson(passCount)),
        ("failCount" -> Json.toJson(failCount)),
        ("avgPassElapsedTime" -> Json.toJson(avgPassElapsedTime)),
        ("avgFailElapsedTime" -> Json.toJson(avgFailElapsedTime)),
        ("maxPassElapsedTime" -> Json.toJson(maxPassElapsedTime)),
        ("maxFailElapsedTime" -> Json.toJson(maxFailElapsedTime)),
        ("failedNodeList" -> Json.toJson(failedNodeList))
        //("totalCount" -> Json.toJson(perNameCount)),

      )
      Insert.InsertAppName(jsobj)
    }
  }
  //println(collection.find("name"->"CSVDetectorV4.py"))
  /*val ob = MongoDBObject("name"->"CSVDetectorV4.py")
  val gamma = collection.count(collection.find,ob)
  println(gamma)*/
}
