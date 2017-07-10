import com.mongodb.casbah.MongoConnection

/**
  * Created by sneema on 06/07/2017 AD.
  */
object MongoFactoryErrorDetails {
  private val SERVER ="localhost"
  private val PORT = 27017
  private val DATABASE = "yarnLogs"
  private val COLLECTION = "PerErrorDetails"

  val connection = MongoConnection()
  val collection = connection(DATABASE)(COLLECTION)
}
