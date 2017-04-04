package io.triplew.example

import argonaut.Argonaut._
import argonaut.CodecJson
import io.finch._
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http
import com.twitter.util.{Await, Future}

import scala.concurrent.{Future => SFuture, Await => SAwait}
import scala.concurrent.duration._

import com.typesafe.config.ConfigFactory
import slick.driver.MySQLDriver.api._

import io.finch._
import io.finch.argonaut._
import scala.util.{Failure, Success, Try}

case class Device(id: Int, deviceId: String)

//TODO zipkin
//TODO onComplete
object Device {
      implicit val deviceCodec: CodecJson[Device] =
              casecodec2(Device.apply, Device.unapply)("id", "device_id")
}

// refs: https://twitter.github.io/finagle/guide/Quickstart.html
object Server extends App {
  import scala.concurrent.ExecutionContext.Implicits.global

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
  
  val deviceApi: Endpoint[List[Device]] = get("device") { 
      val query = sql"select id, device_id from device".as[(Int, String)]
      val f: SFuture[Vector[(Int, String)]] = db.run(query)
      var devices = List.empty[Device]
      val futureDevices = f.map {
          case rows =>
              for (row <- rows) {
                devices = Device(row._1, row._2) :: devices
              }
      }
      SAwait.result(futureDevices, Duration.Inf) 
      
      Ok(devices)
  }

  val userService = (helloWorldApi :+: deviceApi).toService
  val server = Http.server.serve(":8080", userService)
  Await.ready(server)
}
