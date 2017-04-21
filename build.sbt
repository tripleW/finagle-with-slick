name := "finagle-with-slick"

organization := "io.triplew"

version := "0.0.1-SNAPSHOT"

//scalaVersion := "2.12.2"
scalaVersion := "2.11.8"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

val akkaV = "2.4.17"
val slickVersion = "3.1.1"
lazy val mainProject = Project(
  id="finagle-with-slick",
  base=file("."),
  settings = Defaults.coreDefaultSettings ++ Seq(
    scalaVersion := "2.11.8",
    libraryDependencies ++= List(
      "com.typesafe.slick" %% "slick" % slickVersion,
      "com.typesafe.slick" %% "slick-codegen" % slickVersion,
      "com.twitter" %% "finagle-http" % "6.43.0",
      "com.github.finagle" %% "finch-core" % "0.14.0",
      //"com.github.finagle" %% "finch-json4s" % "0.14.0",
      "com.github.finagle"  %% "finch-argonaut"  % "0.14.0",
      "io.argonaut" %% "argonaut" % "6.1",
      "mysql" % "mysql-connector-java" % "5.1.28",
      "com.typesafe.akka" %% "akka-actor"  % akkaV,
      "com.typesafe.akka" %% "akka-slf4j"  % akkaV,
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "io.zipkin.finagle" % "zipkin-finagle-http_2.11" % "0.3.4",
      "com.twitter" % "finagle-stats_2.11" % "6.43.0"
    ),
    slick <<= slickCodeGenTask, // register manual sbt command
    sourceGenerators in Compile <+= slickCodeGenTask // register automatic code generation on every compile, remove for only manual use
  )
)

lazy val slick = TaskKey[Seq[File]]("gen-tables")
lazy val slickCodeGenTask = (sourceManaged, dependencyClasspath in Compile, runner in Compile, streams) map { (dir, cp, r, s) =>
  //val outputDir = (dir / "slick").getPath // place generated files in sbt's managed sources folder
  val outputDir = dir.getPath // place generated files in sbt's managed sources folder
  val url = "jdbc:mysql://0.0.0.0:3307/finagle?user=root" //; INIT=mysql -uroot -h0.0.0.0 -Dfinagle < 'src/main/resources/sql/create.sql'" // connection info for a pre-populated throw-away, in-memory db for this demo, which is freshly initialized on every run
  val jdbcDriver = "com.mysql.jdbc.Driver"
  val slickDriver = "slick.driver.MySQLDriver"
  val pkg = "io.triplew.example.models"
  toError(r.run("slick.codegen.SourceCodeGenerator", cp.files, Array(slickDriver, jdbcDriver, url, outputDir, pkg), s.log))
  val fname = outputDir + "/io/triplew/example/models/Tables.scala"
  Seq(file(fname))
}
