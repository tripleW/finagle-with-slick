package io.triplew.example

import akka.actor.{ActorSystem, Props}
import argonaut.Argonaut._
import argonaut.CodecJson
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http
import java.util.logging.Logger

import com.twitter.util.{Await, Future}
import com.twitter.finagle.stats.{JavaLoggerStatsReceiver, StatsReceiver}
import com.twitter.finagle.tracing.{Record, TraceId}

import scala.concurrent.{ExecutionContextExecutor, Await => SAwait, Future => SFuture}
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import slick.driver.MySQLDriver.api._
import io.finch._
import io.finch.argonaut._
import io.triplew.example.actor.VisitorCountActor
import io.triplew.example.Models._
import io.triplew.example.Models.profile.api._
import zipkin.finagle.http.HttpZipkinTracer
import com.twitter.finagle.zipkin.core.SamplingTracer

case class MyHelper(id: Int, helperId: String, homeGroupName: String, firstName: String, lastName: String)


//TODO zipkin
//TODO onComplete
object MyHelper {
      implicit val deviceCodec: CodecJson[MyHelper] =
              casecodec5(MyHelper.apply, MyHelper.unapply)("id", "helper_id", "home_group_name", "first_name", "last_name")
}

// refs: https://twitter.github.io/finagle/guide/Quickstart.html
object Server extends App {
  implicit val system: ActorSystem  = ActorSystem("finagle-with-slick")
  implicit val executor: ExecutionContextExecutor = system.dispatcher

  val countActor = system.actorOf(Props[VisitorCountActor], "counter")

  val service = new Service[http.Request, http.Response] {
    def apply(req: http.Request): Future[http.Response] =
      Future.value(
        http.Response(req.version, http.Status.Ok)
      )
  }

  val tConfig: HttpZipkinTracer.Config = HttpZipkinTracer.Config.builder().initialSampleRate(1.0f).host("localhost:9411").build()
  val tracer = HttpZipkinTracer.create(tConfig, new JavaLoggerStatsReceiver(Logger.getLogger(getClass.getName)))
  tracer.setSampleRate(1.0f)


  val config = ConfigFactory.load()
  val databaseConfig = config.getConfig("database")

  val jdbcUrl = databaseConfig.getString("url")
  val dbUser = databaseConfig.getString("user")
  val dbPassword = databaseConfig.getString("password")

  val db = Database.forURL(jdbcUrl, driver="com.mysql.jdbc.Driver", user=dbUser, password=dbPassword)

  val helloWorldApi: Endpoint[String] = get("hello") { Ok("Hello, World!") }

  val deviceApi: Endpoint[List[MyHelper]] = get("helpers") {
    /**
      * slick.codegen.SourceCodeGenerator を利用してデータを取得した結果を標準出力
      *
      * refs: http://krrrr38.github.io/slick-doc-ja/v3.0.out/%E3%82%B9%E3%82%AD%E3%83%BC%E3%83%9E%E3%82%B3%E3%83%BC%E3%83%89%E3%81%AE%E7%94%9F%E6%88%90.html
      */
    val q =
      Helper.join(HomeGroup).on(_.homeGroupId === _.homeGroupId)
        .map{ case(h, hg) => (
          h.id,
          h.helperId,
          hg.name.getOrElse(""),
          h.firstName.getOrElse(""),
          h.lastName.getOrElse("")
      )}

    SAwait.result(db.run(q.result).map { rows: Seq[(Int, String, String, String, String)] =>
      rows.foreach {row =>
        println(row._1, row._2, row._3, row._4, row._5)
      }
    }, 60 second)

    /**
      * 下記は、本クラスで定義したクラスMyHelperの結果をjsonで返却
      */
    val query = sql"select id, helper_id, home_group_id, first_name, last_name from helper".as[(Int, String, String, String, String)]
    val eventualVector: SFuture[Vector[(Int, String, String, String, String)]] = db.run(query.transactionally)

    val eventualHelpers = eventualVector.map {f =>
      f.map{ case(id, helperId, homeGroupId, firstName, lastName) => MyHelper(id, helperId, homeGroupId, firstName, lastName)}.toList
    }
    val myHelpers = SAwait.result(eventualHelpers, Duration.Inf)

    println(tracer.getSampleRate)

    countActor ! 'visit
    Ok(myHelpers)
  }

  val userService = (helloWorldApi :+: deviceApi).toService
  import com.twitter.finagle.param
  //val server = Http.server.configured(param.Tracer(tracer)).withTracer(tracer).serve(":8080", userService)
  val server = Http.server.withTracer(tracer).serve(":8080", userService)
  Await.ready(server)
}
