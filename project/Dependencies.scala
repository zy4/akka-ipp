import sbt._

object Dependencies {

  val akkaHttpV  = "10.0.10"
  val circeV     = "0.7.0"
  val scalaMockV = "3.5.0"
  val scalaTestV = "3.0.3"
  val akkaV      = "2.5.6"

  lazy val commonDeps =
    Seq(
      "org.scalatest"     %% "scalatest"                   % scalaTestV % Test,
      "com.typesafe.akka" %% "akka-actor"                  % akkaV,
      "com.typesafe.akka" %% "akka-cluster"                % akkaV,
      "com.typesafe.akka" %% "akka-cluster-tools"          % akkaV,
      "com.typesafe.akka" %% "akka-cluster-metrics"        % akkaV,
      "com.typesafe.akka" %% "akka-slf4j"                  % akkaV,
      "com.typesafe.akka" %% "akka-stream"                 % akkaV,
      "com.typesafe.akka" %% "akka-persistence"            % akkaV,
      "com.typesafe.akka" %% "akka-http"                   % akkaHttpV,
      "de.heikoseeberger" %% "akka-http-circe"             % "1.15.0",
      "io.circe"          %% "circe-core"                  % circeV,
      "io.circe"          %% "circe-generic"               % circeV,
      "io.circe"          %% "circe-parser"                % circeV
    )

}
