package io.triplew.example.actor

import akka.actor.Actor
import akka.event.{Logging, LoggingAdapter}

class VisitorCountActor extends Actor {
  val logger: LoggingAdapter = Logging(context.system, getClass)

  var counter: Int = 0

  def receive = {

    case 'visit =>
      counter += 1
      logger.info(s"""count up ${counter.toString}""")

    case _ =>
      logger.info("this is invalid message")
  }

}
