package it.dtk.api.feed

import javax.ws.rs.Path

import akka.actor.{ActorLogging, Actor, ActorRef}
import akka.http.scaladsl.server.Directives
import akka.util.Timeout
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import io.swagger.annotations._
import it.dtk.api.DefaultJsonFormats
import it.dtk.api.feed.FeedActor._
import it.dtk.news.model.FeedSource
import org.json4s.{DefaultFormats, jackson}

import scala.concurrent.ExecutionContext

/**
  * Created by fabiofumarola on 04/02/16.
  */
@Api(value = "/feed", produces = "application/json")
@Path("/feed")
class FeedService(feedActor: ActorRef)(implicit executionContext: ExecutionContext)
  extends Directives with Json4sSupport {

  implicit val serialization = jackson.Serialization
  // or native.Serialization
  implicit val formats = DefaultFormats

  import akka.pattern.ask

  import scala.concurrent.duration._

  implicit val timeout = Timeout(2.seconds)


  val route = listFeeds ~
    add ~
    del


  @ApiOperation(value = "return the list of the current feeds parsed by wheretolive", notes = "", nickname = "listFeeds", httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return Feeds List", response = classOf[List[FeedSource]]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def listFeeds =
    path("/list") {
      get {
        complete {
          (feedActor ? ListFeeds).mapTo[List[FeedSource]]
        }
      }
    }

  @ApiOperation(value = "add a Feed to be parsed by wheretolive", notes = "", nickname = "addFeed", httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = " feed to add", required = true,
      dataType = "it.dtk.api.feed.FeedActor$AddFeed", paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return a message", response = classOf[String]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def add = path("/add") {
    post {
      entity(as[AddFeed]) { request =>
        complete {
          (feedActor ? request).mapTo[String]
        }
      }
    }
  }

  @ApiOperation(value = "delete a Feed from wheretolive", notes = "", nickname = "delFeed", httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = " feed to delete", required = true,
      dataType = "it.dtk.api.feed.FeedActor$DelFeed", paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return a message", response = classOf[String]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def del = path("feed/del") {
    post {
      entity(as[DelFeed]) { request =>
        complete {
          (feedActor ? request).mapTo[String]
        }
      }
    }
  }
}