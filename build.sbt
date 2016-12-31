name := "Fitter"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.14",
  "com.typesafe.akka" %% "akka-remote" % "2.4.14",
  "com.datastax.cassandra" % "cassandra-driver-core" % "3.1.2",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.slf4j" % "slf4j-nop" % "1.7.22"
)

scalacOptions ++= Seq("-feature")