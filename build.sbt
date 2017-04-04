name := "finagle-with-slick"

organization := "io.triplew"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.8"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  Seq(
    "com.typesafe.slick" % "slick_2.11" % "3.2.0",
    "com.twitter" %% "finagle-http" % "6.43.0",
    "com.github.finagle" %% "finch-core" % "0.14.0",
    //"com.github.finagle" %% "finch-json4s" % "0.14.0",
    "com.github.finagle"  %% "finch-argonaut"  % "0.14.0", 
    "io.argonaut" % "argonaut_2.11" % "6.1",
    "mysql" % "mysql-connector-java" % "5.1.28"
  )
}

    
