import com.mongodb.casbah.MongoConnection

/**
  * Created by sneema on 28/06/2017 AD.
  */
object MongoFactoryAppName  {
  private val SERVER ="localhost"
  private val PORT = 27017
  private val DATABASE = "yarnLogs"
  private val COLLECTION = "applicationName"

  val connection = MongoConnection()
  val collection = connection(DATABASE)(COLLECTION)
}
