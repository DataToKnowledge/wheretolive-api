package it.dtk.api.googlenews

import akka.actor.{Actor, ActorLogging}
import it.dtk.api.googlenews.GoogleNewsActor._
import it.dtk.news.model.FeedSource

/**
  * Created by fabiofumarola on 04/02/16.
  */
object GoogleNewsActor {

  case object ListTerms

  case class DelTerms(terms: List[String])

  case class AddTerms(terms: List[String])

}

class GoogleNewsActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case ListTerms =>
      sender ! List.empty[FeedSource]

    case DelTerms(terms) =>
      sender ! "Success"

    case AddTerms(terms) =>
      sender ! "Success"
  }
}