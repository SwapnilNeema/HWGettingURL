import com.mongodb.casbah.MongoConnection

/**
  * Created by sneema on 22/06/2017 AD.
  */
object MongoFactoryC {
  private  val SERVER ="localhost"
  private val PORT = 27017
  private val DATABASE = "yarnLogs"
  private val COLLECTION = "Containers"
  val connection = MongoConnection()
  val collection = connection(DATABASE)(COLLECTION)
}
