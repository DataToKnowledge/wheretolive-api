package it.dtk.cors

import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.model.headers.{`Access-Control-Allow-Credentials`, `Access-Control-Max-Age`, `Access-Control-Allow-Headers`}

/**
  * Created by fabiofumarola on 04/02/16.
  */
object Main {
  object Main extends App with CorsSupport {
    override val corsAllowOrigins: List[String] = List("*")

    override val corsAllowedHeaders: List[String] = List("Origin", "X-Requested-With", "Content-Type", "Accept", "Accept-Encoding", "Accept-Language", "Host", "Referer", "User-Agent")

    override val corsAllowCredentials: Boolean = true

    override val optionsCorsHeaders: List[HttpHeader] = List[HttpHeader](
      `Access-Control-Allow-Headers`(corsAllowedHeaders.mkString(", ")),
      `Access-Control-Max-Age`(60 * 60 * 24 * 20), // cache pre-flight response for 20 days
      `Access-Control-Allow-Credentials`(corsAllowCredentials)
    )

  }
}
