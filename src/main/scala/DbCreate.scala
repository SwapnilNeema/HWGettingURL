import com.mongodb.DBObject
import com.mongodb.casbah.{MongoClient, MongoClientURI}
import play.api.libs.json.Json

import scala.io.Source

/**
  * Created by sneema on 22/06/2017 AD.
  */
class DbCreate {
  val lines = Source.fromFile("/Users/sneema/Documents/Untitled").getLines().mkString
  val ps2 = Json.parse(lines)
  val dBObject: DBObject = ps2.asInstanceOf[DBObject]
  val mongo = MongoClient(MongoClientURI("mongodb://127.0.0.1:27017"))


}
