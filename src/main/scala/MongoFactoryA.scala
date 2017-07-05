/**
  * Created by sneema on 21/06/2017 AD.
  */
import com.mongodb.casbah.MongoConnection

object MongoFactoryA {
  private  val SERVER ="localhost"
  private val PORT = 27017
  private val DATABASE = "yarnLogs"
  private val COLLECTION = "applicationDetails"
  val connection = MongoConnection()
  val collection = connection(DATABASE)(COLLECTION)

}
