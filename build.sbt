name := """OptAlgoProject"""
organization := "com.example"

version := "1.0-SNAPSHOT"

//lazy val root = (project in file(".")).enablePlugins(PlayScala)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, PlayNettyServer)
  .disablePlugins(PlayAkkaHttpServer)

scalaVersion := "2.13.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "org.scalamock" %% "scalamock" % "4.4.0" % Test
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "2.9.0"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
