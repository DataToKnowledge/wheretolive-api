package it.dtk.api

import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import scala.concurrent.ExecutionContext.Implicits.global

trait Web {
  this: Api with CoreActors with Core =>

  implicit val materializer = ActorMaterializer()

  val host = "0.0.0.0"
  val port = 9000

  val bindingFuture = Http().bindAndHandle(routes, host, port)

  bindingFuture onFailure {
    case ex: Exception =>
      println(s"Failed to bind to ${host}:${port}!")
  }

  bindingFuture.onSuccess {
    case s =>
      println(s"Success to bind to ${s.localAddress.getAddress.toString}:${port}!")
  }

}
