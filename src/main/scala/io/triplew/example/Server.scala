package io.triplew.example

import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http
import com.twitter.util.{Await, Future}

// refs: https://twitter.github.io/finagle/guide/Quickstart.html
object Server extends App {
  val service = new Service[http.Request, http.Response] {
    def apply(req: http.Request): Future[http.Response] =
      Future.value(
        http.Response(req.version, http.Status.Ok)
      )
  }
  val server = Http.serve(":8080", service)
  Await.ready(server)
}
