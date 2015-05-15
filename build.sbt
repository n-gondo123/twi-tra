name := """twi_tra"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
//  jdbc,
//  anorm,
//  cache,
//  ws
  "mysql" % "mysql-connector-java" % "5.1.33",
  "com.typesafe.play" %% "play-slick" % "0.8.1"
)
