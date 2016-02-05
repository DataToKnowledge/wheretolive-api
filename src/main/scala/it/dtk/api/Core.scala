package it.dtk.api

import akka.actor.{Props, ActorSystem}
import it.dtk.api.add.AddActor
import it.dtk.api.feed.FeedActor
import it.dtk.api.hello.HelloActor

/**
  * Core is type containing the ``system: ActorSystem`` member. This enables us to use it in our
  * apps as well as in our tests.
  */
trait Core {

  implicit def system: ActorSystem

}

/**
  * This trait implements ``Core`` by starting the required ``ActorSystem`` and registering the
  * termination handler to stop the system when the JVM exits.
  */
trait BootedCore extends Core {

  /**
    * Construct the ActorSystem we will use in our application
    */
  implicit lazy val system = ActorSystem("akka-spray")

  /**
    * Ensure that the constructed ActorSystem is shut down when the JVM shuts down
    */
  sys.addShutdownHook(system.terminate())

}

/**
  * This trait contains the actors that make up our application; it can be mixed in with
  * ``BootedCore`` for running code or ``TestKit`` for unit and integration tests.
  */
trait CoreActors {
  this: Core =>

  val add = system.actorOf(Props[AddActor])
  val hello = system.actorOf(Props[HelloActor])
  val feed = system.actorOf(Props[FeedActor])
}