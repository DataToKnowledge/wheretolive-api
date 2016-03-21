package it.dtk.api.queryterms

import javax.ws.rs.Path

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives
import akka.util.Timeout
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import io.swagger.annotations._
import it.dtk.api.queryterms.QueryTermActor._
import it.dtk.model._
import org.json4s.ext.JodaTimeSerializers
import org.json4s.jackson.Serialization
import org.json4s.{ NoTypeHints, DefaultFormats, jackson }

import scala.concurrent.ExecutionContext

/**
 * Created by fabiofumarola on 04/02/16.
 */
@Api(value = "/queryterms", produces = "application/json")
@Path("/queryterms")
class QueryTermService(queryTermsActor: ActorRef)(implicit executionContext: ExecutionContext)
    extends Directives with Json4sSupport {

  implicit val serialization = jackson.Serialization
  // or native.Serialization
  implicit val formats = Serialization.formats(NoTypeHints) ++ JodaTimeSerializers.all

  import akka.pattern.ask

  import scala.concurrent.duration._

  implicit val timeout = Timeout(2.seconds)

  val route = listTerms ~
    add ~
    del

  @Path("/list")
  @ApiOperation(value = "return the query terms list used by WhereToLive to extract news", notes = "", nickname = "listQueryTerms", httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return Terms List", response = classOf[List[QueryTerm]]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def listTerms =
    path("queryterms" / "list") {
      get {
        complete {
          (queryTermsActor ? ListTerms).mapTo[List[QueryTerm]]
        }
      }
    }

  @Path("/add")
  @ApiOperation(value = "add new terms in the query terms list used by WhereToLive to extract news", notes = "", nickname = "addQueryTerms", httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = " terms to add", required = true,
      dataType = "it.dtk.api.queryterms.QueryTermActor$AddTerms", paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return a message", response = classOf[String]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def add = path("queryterms" / "add") {
    post {
      entity(as[AddTerms]) { request =>
        complete {
          (queryTermsActor ? request).mapTo[String]
        }
      }
    }
  }

  @Path("/del")
  @ApiOperation(value = "delete existing terms in the query terms list used by WhereToLive to extract news", notes = "", nickname = "delQueryTerms", httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "terms to delete", required = true,
      dataType = "it.dtk.api.queryterms.QueryTermActor$DelTerms", paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return a message", response = classOf[String]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def del = path("queryterms" / "del") {
    post {
      entity(as[DelTerms]) { request =>
        complete {
          (queryTermsActor ? request).mapTo[String]
        }
      }
    }
  }
}