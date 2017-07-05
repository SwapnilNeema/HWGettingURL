import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.Imports._

/**
  * Created by sneema on 21/06/2017 AD.
  */
case class Stock (symbol: String, price: Double)

  object Common {
    /**
      * Convert a Stock object into a BSON format that MongoDb can store.
      */
    def buildMongoDbObject(stock: Stock): MongoDBObject = {
      val builder = MongoDBObject.newBuilder
      builder += "symbol" -> stock.symbol
      builder += "price" -> stock.price
      builder.result
    }
  }

