import Dependencies._

lazy val root = (project in file(".")).settings(
  inThisBuild(
    List(
      organization := "de.envisia",
      scalaVersion := "2.12.4",
      version := "0.1.0-SNAPSHOT"
    )
  ),
  name := "akka-ipp",
  libraryDependencies ++= Dependencies.commonDeps
)

scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")
