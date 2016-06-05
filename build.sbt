name := """poc_frontend"""

version := "1.0-SNAPSHOT"
lazy val root = (project in file(".")).enablePlugins(PlayScala)


scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
    jdbc,
    cache,
    ws,
    "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
    "com.typesafe.akka" %% "akka-actor" % "2.4.4",
    "com.typesafe.akka" %% "akka-stream" % "2.4.4",
    "com.typesafe.akka" %% "akka-http-experimental" % "2.4.4",
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.4",
    "com.typesafe.akka" %% "akka-http-testkit" % "2.4.4"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"