package io.triplew.example.actor

import akka.actor.Actor
import akka.event.{Logging, LoggingAdapter}
import com.twitter.finagle.stats.MetricsHostStatsReceiver
import com.twitter.finagle.{Http, http}
import com.twitter.util.Future
import zipkin.finagle.http.HttpZipkinTracer

class VisitorCountActor extends Actor {
  val logger: LoggingAdapter = Logging(context.system, getClass)

  var counter: Int = 0

  val hostStatsReceiver = new MetricsHostStatsReceiver()
  val tConfig: HttpZipkinTracer.Config = HttpZipkinTracer.Config.builder().initialSampleRate(1.0f).host("0.0.0.0:9411").build()
  val tracer = HttpZipkinTracer.create(tConfig, hostStatsReceiver)
  tracer.setSampleRate(1.0f)

  val client =
    Http.client
      .withTracer(tracer)
      .withLabel("visitor-count-actor")
      .newService("www.scala-lang.org:80")

  def receive = {

    case 'visit =>
      counter += 1
      logger.info(s"""count up ${counter.toString}""")

      val request = http.Request(http.Method.Get, "/")
      request.host = "www.scala-lang.org"
      val response: Future[http.Response] = client(request)
      response.map {f =>
        logger.info(f.statusCode.toString)
      }

    case _ =>
      logger.info("this is invalid message")
  }

}
