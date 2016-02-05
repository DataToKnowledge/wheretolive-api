package it.dtk.api

import it.dtk.api.add.AddService
import it.dtk.api.feed.FeedService
import it.dtk.api.hello.HelloService

import scala.util.control.NonFatal
import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.server.RouteConcatenation
import it.dtk.api.swagger.SwaggerDocService

/**
  * The REST API layer. It exposes the REST services, but does not provide any
  * web server interface.<br/>
  * Notice that it requires to be mixed in with ``core.CoreActors``, which provides access
  * to the top-level actors that make up the system.
  */
trait Api extends RouteConcatenation with CorsSupport {
  this: CoreActors with Core =>

  private implicit val _ = system.dispatcher

  val routes =
    new AddService(add).route ~
      new HelloService(hello).route ~
      new FeedService(feed).route ~
      corsHandler(new SwaggerDocService(system).routes)

}
