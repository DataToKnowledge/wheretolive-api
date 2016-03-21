package it.dtk.api.search

import javax.ws.rs.Path

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives
import akka.util.Timeout
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import io.swagger.annotations._
import it.dtk.api.search.SearchActor.Search
import org.json4s.ext.JodaTimeSerializers
import org.json4s.jackson.Serialization
import org.json4s.{ NoTypeHints, DefaultFormats, jackson }

import scala.concurrent.ExecutionContext

/**
 * Created by fabiofumarola on 04/02/16.
 */
@Api(value = "/search", produces = "application/json")
@Path("/search")
class SearchService(searchActor: ActorRef)(implicit executionContext: ExecutionContext)
    extends Directives with Json4sSupport {

  implicit val serialization = jackson.Serialization
  implicit val formats = Serialization.formats(NoTypeHints) ++ JodaTimeSerializers.all

  import akka.pattern.ask

  import scala.concurrent.duration._

  implicit val timeout = Timeout(20.seconds)

  val routes = search

  @Path("/search")
  @ApiOperation(value = "search endpoint", notes = "", nickname = "search", httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "", required = true,
      dataType = "it.dtk.api.search.SearchActor$Search", paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return a message", response = classOf[String]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def search = path("search" / "search") {
    post {
      entity(as[Search]) { request =>
        complete {
          (searchActor ? request).mapTo[String]
        }
      }
    }
  }

}