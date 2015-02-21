name := "oooserver"

version := "1.0"

scalaVersion := "2.11.4"

javaOptions ++= Seq(
  "-Xms512M", "-Xmx2G", "-Xss1M",
  "-XX:+CMSClassUnloadingEnabled",
  "-XX:+UseConcMarkSweepGC"
)

//see https://github.com/scala/scala/blob/2.10.x/src/compiler/scala/tools/nsc/settings/ScalaSettings.scala
scalacOptions ++= Seq(
  "-encoding", "UTF-8", "-deprecation", "-unchecked", "-feature", //"-optimise",
  "-Xmigration", //"–Xverify", "-Xcheck-null", "-Ystatistics",
  "-Yinline-warnings", "-Ywarn-dead-code", "-Ydead-code"
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.scalamock" %% "scalamock-scalatest-support" % "3.1.1" % "test",
  "com.typesafe.akka" %% "akka-slf4j" % "2.3.3",
  "com.typesafe.akka" %% "akka-actor" % "2.3.3",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.3",
  "org.scala-lang.modules" %% "scala-async" % "0.9.2",
  "com.typesafe.play" %% "play-json" % "2.3.1",
  "org.apache.logging.log4j" % "log4j-api" % "2.0.2",
  "org.apache.logging.log4j" % "log4j-core" % "2.0.2",
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.0.2",
  "net.debasishg" %% "redisreact" % "0.7",
  "com.jason-goodwin" %% "authentikat-jwt" % "0.3.5"
)

resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
  "Typesafe simple" at "http://repo.typesafe.com/typesafe/simple/maven-releases/"
)
