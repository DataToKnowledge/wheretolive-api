package it.dtk.api

import akka.actor.{ ActorSystem, Props }
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{ Route, Directives, RouteConcatenation }
import akka.stream.ActorMaterializer
import com.github.swagger.akka.model.Info
import com.github.swagger.akka.{ HasActorSystem, SwaggerHttpService }
import it.dtk.api.feed.{ FeedActor, FeedService }
import it.dtk.api.queryterms.{ QueryTermActor, QueryTermService }
import it.dtk.api.search.{ SearchActor, SearchService }
import ch.megard.akka.http.cors._

import scala.reflect.runtime.universe
import scala.util.{ Failure, Success }

object Web extends App with RouteConcatenation with Directives {

  implicit val system = ActorSystem("akka-http")
  implicit val materializer = ActorMaterializer()
  implicit val executor = system.dispatcher
  sys.addShutdownHook(system.terminate())

  val confFile = "mac_dev.conf"
  val host = "0.0.0.0"
  val port = 9000

  //actors to be started
  val queryTerms = system.actorOf(Props[QueryTermActor])
  val feed = system.actorOf(Props[FeedActor])
  val search = system.actorOf(Props(new SearchActor(confFile)))

  val swaggerUI = path("swagger") {
    getFromResource("swagger/index.html")
  } ~ getFromResourceDirectory("swagger")

  val routes: Route =
    new QueryTermService(queryTerms).route ~
      new FeedService(feed).routes ~
      new SearchService(search).routes ~
      new SwaggerDocService(system).routes ~
      swaggerUI

  val bindingFuture = Http().bindAndHandle(routes, host, port)

  bindingFuture onComplete {
    case Success(value) =>
      println(s"Success to bind to ${value.localAddress.getAddress.toString}:${port}!")

    case Failure(ex) =>
      println(s"Failed to bind to ${host}:${port}!")
  }
}

class SwaggerDocService(system: ActorSystem) extends SwaggerHttpService with HasActorSystem {
  override implicit val actorSystem: ActorSystem = system
  override implicit val materializer: ActorMaterializer = ActorMaterializer()
  override val apiTypes = Seq(
    universe.typeOf[FeedService],
    universe.typeOf[QueryTermService],
    universe.typeOf[SearchService]
  )
  override val host = "api.datatoknowledge.it"
  override val info = Info(version = "1.0")
}