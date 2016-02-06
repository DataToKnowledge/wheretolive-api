package it.dtk.api

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.model.headers.{`Access-Control-Allow-Credentials`, `Access-Control-Allow-Headers`, `Access-Control-Max-Age`}
import akka.http.scaladsl.server.RouteConcatenation
import akka.stream.ActorMaterializer
import com.github.swagger.akka.model.Info
import com.github.swagger.akka.{HasActorSystem, SwaggerHttpService}
import it.dtk.api.add.{AddActor, AddService}
import it.dtk.api.feed.{FeedActor, FeedService}
import it.dtk.api.hello.{HelloActor, HelloService}
import akka.http.scaladsl.server.Directives

import scala.reflect.runtime.universe
import scala.util.{Failure, Success}

/**
  * The REST API layer. It exposes the REST services, but does not provide any
  * web server interface.<br/>
  * Notice that it requires to be mixed in with ``core.CoreActors``, which provides access
  * to the top-level actors that make up the system.
  */
trait Api extends RouteConcatenation with AkkaHttpCorsSupport with Directives {

  override protected def corsAllowOrigins: List[String] = List("*")

  override protected def corsAllowCredentials: Boolean = true

  override protected def optionsCorsHeaders: List[HttpHeader] = List[HttpHeader](
    `Access-Control-Allow-Headers`(corsAllowedHeaders.mkString(", ")),
    `Access-Control-Max-Age`(60 * 60 * 24 * 20), // cache pre-flight response for 20 days
    `Access-Control-Allow-Credentials`(corsAllowCredentials)
  )

  override protected def corsAllowedHeaders: List[String] =
    List("Origin", "X-Requested-With", "Content-Type", "Accept", "Accept-Encoding", "Accept-Language", "Host", "Referer", "User-Agent")

  /**
    * Construct the ActorSystem we will use in our application
    */
  implicit lazy val system = ActorSystem("akka-http")

  /**
    * Ensure that the constructed ActorSystem is shut down when the JVM shuts down
    */
  sys.addShutdownHook(system.terminate())

  val add = system.actorOf(Props[AddActor])
  val hello = system.actorOf(Props[HelloActor])
  val feed = system.actorOf(Props[FeedActor])

  private implicit val _ = system.dispatcher

  val swaggerUI = path("swagger") {
    getFromResource("swagger/index.html")
  } ~ getFromResourceDirectory("swagger")

  val routes =
    new AddService(add).route ~
      new HelloService(hello).route ~
      new FeedService(feed).route ~
      new SwaggerDocService(system).routes ~
      swaggerUI


}

class SwaggerDocService(system: ActorSystem) extends SwaggerHttpService with HasActorSystem {
  override implicit val actorSystem: ActorSystem = system
  override implicit val materializer: ActorMaterializer = ActorMaterializer()
  override val apiTypes = Seq(universe.typeOf[AddService], universe.typeOf[HelloService], universe.typeOf[FeedService])
  override val host = "localhost:9000"
  override val info = Info(version = "1.0")
}

object Web extends App with Api {


  implicit val materializer = ActorMaterializer()
  implicit val executor = materializer.executionContext

  val host = "0.0.0.0"
  val port = 9000

  val bindingFuture = Http().bindAndHandle(routes, host, port)

  bindingFuture onComplete {
    case Success(value) =>
      println(s"Success to bind to ${value.localAddress.getAddress.toString}:${port}!")

    case Failure(ex) =>
      println(s"Failed to bind to ${host}:${port}!")
  }
}
