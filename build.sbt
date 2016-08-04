import com.typesafe.config.ConfigFactory

organization := "com.wavesplatform"

name := "matcher"

version := "0.1.0"

scalaVersion := "2.11.8"

resolvers += "SonaType" at "https://oss.sonatype.org/content/groups/public"

val modulesVersion = "1.3.3-SNAPSHOT"

libraryDependencies ++= Seq(
  "com.wavesplatform" %% "scorex-basics" % modulesVersion,
  "com.wavesplatform" %% "scorex-consensus" % modulesVersion,
  "com.wavesplatform" %% "scorex-transaction" % modulesVersion,
  "io.spray" %% "spray-testkit" % "1.+" % "test",
  "org.scalatest" %% "scalatest" % "2.+" % "test",
  "org.scalactic" %% "scalactic" % "2.+" % "test",
  "org.scalacheck" %% "scalacheck" % "1.12.+" % "test",
  "net.databinder.dispatch" %% "dispatch-core" % "+" % "test"
)

