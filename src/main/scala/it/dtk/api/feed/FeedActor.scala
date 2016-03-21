package it.dtk.api.feed

import akka.actor.{ ActorLogging, Actor }
import it.dtk.api.feed.FeedActor.{ ListFeeds, AddFeed, DelFeed }
import it.dtk.model._

/**
 * Created by fabiofumarola on 04/02/16.
 */
object FeedActor {

  case object ListFeeds

  case class DelFeed(url: String)

  case class AddFeed(url: String, publisher: String)

}

class FeedActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case ListFeeds =>
      sender ! List.empty[Feed]

    case DelFeed(url) =>
      sender ! "Success"

    case AddFeed(url, publisher) =>
      sender ! "Success"
  }
}
