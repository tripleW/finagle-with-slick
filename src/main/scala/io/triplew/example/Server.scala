package io.triplew.example

import akka.actor.{ActorSystem, Props}
import argonaut.Argonaut._
import argonaut.CodecJson
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http
import com.twitter.util.{Await, Future}

import scala.concurrent.{ExecutionContextExecutor, Await => SAwait, Future => SFuture}
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import slick.driver.MySQLDriver.api._
import io.finch._
import io.finch.argonaut._
import io.triplew.example.actor.VisitorCountActor

case class Helper(id: Int, helperId: String, homeGroupId: String, firstName: String, lastName: String)

//TODO zipkin
//TODO onComplete
object Helper {
      implicit val deviceCodec: CodecJson[Helper] =
              casecodec5(Helper.apply, Helper.unapply)("id", "helper_id", "home_group_id", "first_name", "last_name")
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

  val config = ConfigFactory.load()
  val databaseConfig = config.getConfig("database")
  
  val jdbcUrl = databaseConfig.getString("url")
  val dbUser = databaseConfig.getString("user")
  val dbPassword = databaseConfig.getString("password")

  val db = Database.forURL(jdbcUrl, driver="com.mysql.jdbc.Driver", user=dbUser, password=dbPassword)

  val helloWorldApi: Endpoint[String] = get("hello") { Ok("Hello, World!") }
  
  val deviceApi: Endpoint[List[Helper]] = get("helpers") {
    val query = sql"select id, helper_id, home_group_id, first_name, last_name from helper".as[(Int, String, String, String, String)]
    val eventualVector: SFuture[Vector[(Int, String, String, String, String)]] = db.run(query.transactionally)

    val eventualHelpers = eventualVector.map {f =>
      f.map{ case(id, helperId, homeGroupId, firstName, lastName) => Helper(id, helperId, homeGroupId, firstName, lastName)}.toList
    }
    val helpers = SAwait.result(eventualHelpers, Duration.Inf)

    countActor ! 'visit
    Ok(helpers)
  }

  val userService = (helloWorldApi :+: deviceApi).toService
  val server = Http.server.serve(":8080", userService)
  Await.ready(server)
}
