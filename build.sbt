name := "Spark Streaming Example"

organization := "com.constructiveproof"

version := "1.0"

scalaVersion := "2.10.3"

crossScalaVersions := Seq("2.10.3", "2.11.0-M8")

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.2.1",
  "org.apache.spark" %% "spark-core" % "0.9.1",
  "org.apache.spark" %% "spark-streaming-twitter" % "0.9.1",
  "org.scalatest" %% "scalatest" % "2.1.RC1" % "test",
  "org.scalacheck" %% "scalacheck" % "1.11.3" % "test"
)

initialCommands := "import com.constructiveproof.sparkstreamingexample._"
