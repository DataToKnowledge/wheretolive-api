package it.dtk.api.googlenews

import javax.ws.rs.Path

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives
import akka.util.Timeout
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import io.swagger.annotations._
import it.dtk.api.googlenews.GoogleNewsActor._
import it.dtk.model._
import org.json4s.{ DefaultFormats, jackson }

import scala.concurrent.ExecutionContext

/**
 * Created by fabiofumarola on 04/02/16.
 */
@Api(value = "/google-news", produces = "application/json")
@Path("/google-news")
class GoogleNewsService(googleNewsActor: ActorRef)(implicit executionContext: ExecutionContext)
    extends Directives with Json4sSupport {

  implicit val serialization = jackson.Serialization
  // or native.Serialization
  implicit val formats = DefaultFormats

  import akka.pattern.ask

  import scala.concurrent.duration._

  implicit val timeout = Timeout(2.seconds)

  val route = listTerms ~
    add ~
    del

  @Path("/list")
  @ApiOperation(value = "return the goole-news terms list used by WhereToLSive to extract google's news", notes = "", nickname = "listGoogleTerms", httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return Feeds List", response = classOf[List[QueryTerm]]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def listTerms =
    path("google-news" / "list") {
      get {
        complete {
          (googleNewsActor ? ListTerms).mapTo[List[QueryTerm]]
        }
      }
    }

  @Path("/add")
  @ApiOperation(value = "add new terms in the google-news' terms list used by WhereToTive to extract google's news", notes = "", nickname = "addGoogleTerms", httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = " feed to add", required = true,
      dataType = "it.dtk.api.feed.FeedActor$AddFeed", paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return a message", response = classOf[String]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def add = path("google-news" / "add") {
    post {
      entity(as[AddTerms]) { request =>
        complete {
          (googleNewsActor ? request).mapTo[String]
        }
      }
    }
  }

  @Path("/del")
  @ApiOperation(value = "delete existing terms in the google-news' terms list used by WhereToLive to extract google's news", notes = "", nickname = "delGoogleTerms", httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = " feed to delete", required = true,
      dataType = "it.dtk.api.feed.FeedActor$DelFeed", paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return a message", response = classOf[String]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def del = path("google-news" / "del") {
    post {
      entity(as[DelTerms]) { request =>
        complete {
          (googleNewsActor ? request).mapTo[String]
        }
      }
    }
  }
}