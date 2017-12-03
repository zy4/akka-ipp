import sbt._

object Dependencies {

  val akkaHttpV  = "10.0.11"
  val scalaTestV = "3.0.3"
  val akkaV      = "2.5.7"
  val slf4jV     = "1.7.25"

  lazy val commonDeps =
    Seq(
      "org.scalatest"     %% "scalatest"           % scalaTestV % Test,
      "com.typesafe.akka" %% "akka-actor"          % akkaV,
      "com.typesafe.akka" %% "akka-slf4j"          % akkaV,
      "com.typesafe.akka" %% "akka-stream"         % akkaV,
      "com.typesafe.akka" %% "akka-http"           % akkaHttpV,
      "com.typesafe.akka" %% "akka-http-testkit"   % akkaHttpV % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaV % Test,
      "org.slf4j"         % "slf4j-api"            % slf4jV,
      "org.slf4j"         % "slf4j-simple"         % slf4jV
    )

}
