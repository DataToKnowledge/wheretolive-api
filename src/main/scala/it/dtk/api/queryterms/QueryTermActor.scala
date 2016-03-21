package it.dtk.api.queryterms

import akka.actor.{ Actor, ActorLogging }
import it.dtk.api.queryterms.QueryTermActor._
import it.dtk.model.Feed

/**
 * Created by fabiofumarola on 04/02/16.
 */
object QueryTermActor {

  case object ListTerms

  case class DelTerms(terms: List[String])

  case class AddTerms(terms: List[String])

}

class QueryTermActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case ListTerms =>
      sender ! List.empty[Feed]

    case DelTerms(terms) =>
      sender ! "Success"

    case AddTerms(terms) =>
      sender ! "Success"
  }
}