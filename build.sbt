name := "finagle-with-slick"

organization := "io.triplew"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.8"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  Seq(
    "com.typesafe.slick" % "slick_2.11" % "3.2.0",
    "com.twitter" %% "finagle-http" % "6.43.0",
    "mysql" % "mysql-connector-java" % "5.1.28"
  )
}

    