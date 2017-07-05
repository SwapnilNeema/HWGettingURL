import com.mongodb.casbah.MongoConnection

/**
  * Created by sneema on 23/06/2017 AD.
  */
object MongoFactoryAttemptState {
  private  val SERVER ="localhost"
  private val PORT = 27017
  private val DATABASE = "yarnLogs"
  private val COLLECTION = "attemptState"
  val connection = MongoConnection()
  val collection = connection(DATABASE)(COLLECTION)
}
