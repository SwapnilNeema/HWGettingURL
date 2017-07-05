import com.mongodb.casbah.MongoConnection

/**
  * Created by sneema on 26/06/2017 AD.
  */
object MongoFactoryAppState {
  private val PORT = 27017
  private val DATABASE = "yarnLogs"
  private val COLLECTION = "appState"
  val connection = MongoConnection()
  val collection = connection(DATABASE)(COLLECTION)
}
