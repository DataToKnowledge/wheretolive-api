package it.dtk.api.feed

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives
import akka.util.Timeout
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import io.swagger.annotations._
import it.dtk.api.feed.FeedActor._
import it.dtk.model._
import org.json4s.ext.JodaTimeSerializers
import org.json4s.jackson.Serialization
import org.json4s.{ NoTypeHints, DefaultFormats, jackson }
import javax.ws.rs.Path
import scala.concurrent.ExecutionContext

/**
 * Created by fabiofumarola on 04/02/16.
 */
@Api(value = "/feed", produces = "application/json")
@Path("/feed")
class FeedService(feedActor: ActorRef)(implicit executionContext: ExecutionContext)
    extends Directives with Json4sSupport {

  implicit val serialization = jackson.Serialization
  implicit val formats = Serialization.formats(NoTypeHints) ++ JodaTimeSerializers.all

  import akka.pattern.ask
  import scala.concurrent.duration._

  implicit val timeout = Timeout(2.seconds)

  val routes = listFeeds ~
    add ~
    del

  @Path("/list")
  @ApiOperation(value = "return the list of the current feeds parsed by wheretolive", notes = "", nickname = "listFeeds", httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return Feeds List", response = classOf[List[Feed]]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def listFeeds =
    path("feed" / "list") {
      get {
        complete {
          (feedActor ? ListFeeds).mapTo[List[Feed]]
        }
      }
    }

  @Path("/add")
  @ApiOperation(value = "add a Feed to be parsed by wheretolive", notes = "", nickname = "addFeed", httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = " feed to add", required = true,
      dataType = "it.dtk.api.feed.FeedActor$AddFeed", paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return a message", response = classOf[String]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def add = path("feed" / "add") {
    post {
      entity(as[AddFeed]) { request =>
        complete {
          (feedActor ? request).mapTo[String]
        }
      }
    }
  }

  @Path("/del")
  @ApiOperation(value = "delete a Feed from wheretolive", notes = "", nickname = "delFeed", httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = " feed to delete", required = true,
      dataType = "it.dtk.api.feed.FeedActor$DelFeed", paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return a message", response = classOf[String]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def del = path("feed" / "del") {
    post {
      entity(as[DelFeed]) { request =>
        complete {
          (feedActor ? request).mapTo[String]
        }
      }
    }
  }
}
