package it.dtk.api.search

import akka.actor.{ ActorLogging, Actor }
import com.typesafe.config.ConfigFactory
import it.dtk.es.ElasticArticles
import net.ceedubs.ficus.Ficus._
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import scala.util.{ Failure, Success }

object SearchActor {

  case class Search(request: JValue)
  type Query = JValue

}

/**
 * Created by fabiofumarola on 21/03/16.
 */
class SearchActor(configFile: String) extends Actor with ActorLogging {

  import context.dispatcher

  import it.dtk.api.search.SearchActor._

  val config = ConfigFactory.load(configFile).getConfig("reactive_wtl")

  val esHosts = config.as[String]("elastic.hosts")
  val searchHost = config.as[String]("elastic.searchHost")
  val indexPath = config.as[String]("elastic.docs.articles")
  val clusterName = config.as[String]("elastic.clusterName")

  val service = new ElasticArticles(esHosts, searchHost, indexPath, clusterName)

  override def receive: Receive = {

    case Search(query) =>
      val send = sender
      log.debug("processing query {}", query)
      service.rawQuery(query) onComplete {
        case Success(res) => send ! res
        case Failure(ex) =>
          log.error(ex, "error")
          send ! render("error" -> ex.getLocalizedMessage)
      }

    case query: Query =>
      val send = sender
      log.debug("processing query {}", query)
      service.rawQuery(query) onComplete {
        case Success(res) => send ! res
        case Failure(ex) =>
          log.error(ex, "error")
          send ! render("error" -> ex.getLocalizedMessage)
      }
  }
}
