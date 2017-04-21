name := "finagle-with-slick"

organization := "io.triplew"

version := "0.0.1-SNAPSHOT"

//scalaVersion := "2.12.2"
scalaVersion := "2.11.8"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.4.17"
  Seq(
    "com.typesafe.slick" %% "slick" % "3.2.0",
    "com.twitter" %% "finagle-http" % "6.43.0",
    "com.github.finagle" %% "finch-core" % "0.14.0",
    //"com.github.finagle" %% "finch-json4s" % "0.14.0",
    "com.github.finagle"  %% "finch-argonaut"  % "0.14.0", 
    "io.argonaut" %% "argonaut" % "6.1",
    "mysql" % "mysql-connector-java" % "5.1.28",
    "com.typesafe.akka" %% "akka-actor"  % akkaV,
    "com.typesafe.akka" %% "akka-slf4j"  % akkaV
  )
}

    
