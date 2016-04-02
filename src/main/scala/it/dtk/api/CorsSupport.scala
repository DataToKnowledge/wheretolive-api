package it.dtk.api

/**
 * Created by fabiofumarola on 02/04/16.
 */

import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ Directive0, Route }
import com.typesafe.config.ConfigFactory

trait CorsSupport {
  lazy val allowedOriginHeader = {
    //    val config = ConfigFactory.load()
    //    val sAllowedOrigin = "*" //config.getString("cors.allowed-origin")
    //    if (sAllowedOrigin == "*")
    `Access-Control-Allow-Origin`.*
    //    else
    //      `Access-Control-Allow-Origin`(HttpOrigin(sAllowedOrigin))
  }

  private def addAccessControlHeaders: Directive0 = {
    mapResponseHeaders { headers =>
      allowedOriginHeader +:
        `Access-Control-Allow-Credentials`(true) +:
        `Access-Control-Allow-Headers`("X-Requested-With", "content-type", "origin", "accept") +:
        headers
    }
  }

  private def preflightRequestHandler: Route = options {
    complete(HttpResponse(200).withHeaders(
      `Access-Control-Allow-Methods`(OPTIONS, POST, PUT, GET, DELETE, CONNECT, HEAD, PATCH, TRACE)
    ))
  }

  def corsHandler(r: Route) = addAccessControlHeaders {
    preflightRequestHandler ~ r
  }
}
