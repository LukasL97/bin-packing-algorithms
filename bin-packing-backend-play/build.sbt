
name := """bin-packing-backend"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, PlayNettyServer)
  .enablePlugins(PackPlugin)
  .disablePlugins(PlayAkkaHttpServer)

// needed to run docker image created via sbt docker:publishLocal
javaOptions in Universal ++= Seq(
  "-Dpidfile.path=/dev/null"
)

dockerExposedPorts ++= Seq(9000)
dockerUpdateLatest := true

scalaVersion := "2.13.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "org.scalamock" %% "scalamock" % "4.4.0" % Test
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "2.9.0"
libraryDependencies += "org.json4s" %% "json4s-native" % "3.7.0-M8"
libraryDependencies += "io.dropwizard.metrics" % "metrics-core" % "4.1.0"
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.6.10" % Test
libraryDependencies += "com.github.scopt" %% "scopt" % "4.0.1"
libraryDependencies += "com.nrinaudo" %% "kantan.csv" % "0.6.1"
libraryDependencies += "com.nrinaudo" %% "kantan.csv-generic" % "0.6.1"

herokuAppName in Compile := "bin-packing-backend"

