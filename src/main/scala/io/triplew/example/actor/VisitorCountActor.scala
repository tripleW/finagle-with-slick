package io.triplew.example.actor

import akka.actor.Actor
import akka.event.{Logging, LoggingAdapter}
import com.twitter.finagle.Http
import zipkin.finagle.http.HttpZipkinTracer

class VisitorCountActor extends Actor {
  val logger: LoggingAdapter = Logging(context.system, getClass)

  var counter: Int = 0

  def receive = {

    case 'visit =>
      counter += 1
      logger.info(s"""count up ${counter.toString}""")

      /* todo call other host
      val client =
        Http.client
          .withTracer(tracer = new HttpZipkinTracer())
          .withLabel("trace-sample")
          .newService("0.0.0.0:8080/traces")
      */

    case _ =>
      logger.info("this is invalid message")
  }

}
