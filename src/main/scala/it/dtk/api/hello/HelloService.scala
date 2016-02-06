package it.dtk.api.hello

import javax.ws.rs.Path

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives
import akka.util.Timeout
import io.swagger.annotations._
import it.dtk.api.DefaultJsonFormats
import it.dtk.api.hello.HelloActor._

import scala.concurrent.ExecutionContext

@Api(value = "/hello", description = "Hello Template.", produces = "application/json")
@Path("/hello")
class HelloService(hello: ActorRef)(implicit executionContext: ExecutionContext)
  extends Directives with DefaultJsonFormats {

  import akka.pattern.ask

  import scala.concurrent.duration._

  implicit val timeout = Timeout(2.seconds)
  implicit val greetingFormat = jsonFormat1(Greeting)

  val route =
    getHello ~
    getHelloSegment
    
  @ApiOperation(value = "Return Hello greeting", notes = "", nickname = "anonymousHello", httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return Hello Greeting", response = classOf[Greeting]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def getHello =
    path("hello") {
      get {
        complete { (hello ? AnonymousHello).mapTo[Greeting] }
      }
    } 
    
  @Path("/{name}")
  @ApiOperation(value = "Return Hello greeting with person's name", notes = "", nickname = "hello", httpMethod = "GET")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "name", value = "Name of person to greet", required = false, dataType = "string", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return Hello Greeting", response = classOf[Greeting]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def getHelloSegment =
    path("hello" / Segment) { name =>
      get {
        complete { (hello ? Hello(name)).mapTo[Greeting] }
      }
    }
}

