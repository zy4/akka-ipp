import sbt._

object Dependencies {

  val akkaHttpV = "10.1.2"
  val uTestV    = "0.6.3"
  val akkaV     = "2.5.13"
  val slf4jV    = "1.7.25"
  val tikaV     = "1.16"

  lazy val commonDeps =
    Seq(
      "com.lihaoyi"       %% "utest"               % uTestV % Test,
      "com.typesafe.akka" %% "akka-actor"          % akkaV,
      "com.typesafe.akka" %% "akka-slf4j"          % akkaV,
      "com.typesafe.akka" %% "akka-stream"         % akkaV,
      "com.typesafe.akka" %% "akka-http"           % akkaHttpV,
      "com.typesafe.akka" %% "akka-http-testkit"   % akkaHttpV % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaV % Test,
      "org.slf4j"         % "slf4j-api"            % slf4jV,
      "org.slf4j"         % "slf4j-simple"         % slf4jV % Test,
      "org.apache.tika"   % "tika-core"            % tikaV % Test,
      "org.apache.tika"   % "tika-parsers"         % tikaV % Test
    )

}
