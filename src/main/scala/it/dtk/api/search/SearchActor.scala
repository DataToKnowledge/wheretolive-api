package it.dtk.api.search

import akka.actor.{ ActorLogging, Actor }

object SearchActor {

  case class Search(query: String)

}

/**
 * Created by fabiofumarola on 21/03/16.
 */
class SearchActor extends Actor with ActorLogging {

  import it.dtk.api.search.SearchActor._

  override def receive: Receive = {
    case Search(query) =>
      sender ! s"Success $query"
  }
}
