import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject
import play.api.libs.json.JsObject

/**
  * Created by sneema on 22/06/2017 AD.
  */
 object Insert extends App {

  def InsertDBApp(jsobj : JsObject)  {
    val newObj = MongoDBObject(jsobj.toString())
    saveObject(newObj)

    def saveObject(obj: DBObject): Unit = {
      MongoFactoryA.collection.save(obj)
    }
  }
  def InsertDBAppAttempt(jsobj: JsObject): Unit ={
    val newObj = MongoDBObject(jsobj.toString())
    saveObject(newObj)

    def saveObject(obj: DBObject): Unit = {
      MongoFactoryAt.collection.save(obj)
    }
  }
  def InsertDBContainer(jsobj: JsObject): Unit ={
    val newObj = MongoDBObject(jsobj.toString())
    saveObject(newObj)

    def saveObject(obj: DBObject): Unit = {
      MongoFactoryC.collection.save(obj)
    }
  }
  def InsertAppAttemptState(jsobj: JsObject): Unit ={
    val newObj = MongoDBObject(jsobj.toString())
    saveObject(newObj)
    def saveObject(obj: DBObject): Unit ={
      MongoFactoryAttemptState.collection.save(obj)
    }
  }
  def InsertAppAState(jsobj: JsObject): Unit ={
    val newObj = MongoDBObject(jsobj.toString())
    saveObject(newObj)
    def saveObject(obj: DBObject): Unit ={
      MongoFactoryAppState.collection.save(obj)
    }
  }
  def InsertAppName(jsobj: JsObject): Unit ={
    val newObj = MongoDBObject(jsobj.toString())
    saveObject(newObj)
    def saveObject(obj: DBObject): Unit ={
      MongoFactoryAppName.collection.save(obj)
    }
  }


}
/*  val apple = Stock("AAPLE",600)
    val google = Stock("GOOGle",650)
    val netflix = Stock("netflix",7a00)
  saveStock(apple)
  saveStock(google)
  saveStock(netflix)

  def saveStock(stock: Stock): Unit ={
    val mongoObj = buildMongoDbObject(stock)
    MongoFactory.collection.save(mongoObj)
  }*/