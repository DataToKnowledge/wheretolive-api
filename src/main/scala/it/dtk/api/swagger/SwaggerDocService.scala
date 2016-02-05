package it.dtk.api.swagger

import com.github.swagger.akka.model.Info
import it.dtk.api.add.AddService
import it.dtk.api.feed.FeedService
import it.dtk.api.hello.HelloService

import scala.reflect.runtime.{universe=>ru}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.github.swagger.akka._

class SwaggerDocService(system: ActorSystem) extends SwaggerHttpService with HasActorSystem {
  override implicit val actorSystem: ActorSystem = system
  override implicit val materializer: ActorMaterializer = ActorMaterializer()
  override val apiTypes = Seq(ru.typeOf[AddService], ru.typeOf[HelloService], ru.typeOf[FeedService])
  override val host = "localhost:9000"
  override val info = Info(version = "1.0")
}