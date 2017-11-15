import sbt._

object Dependencies {

  val akkaHttpV  = "10.0.10"
  val scalaTestV = "3.0.3"
  val akkaV      = "2.5.6"

  lazy val commonDeps =
    Seq(
      "org.scalatest"     %% "scalatest"           % scalaTestV % Test,
      "com.typesafe.akka" %% "akka-actor"          % akkaV,
      "com.typesafe.akka" %% "akka-slf4j"          % akkaV,
      "com.typesafe.akka" %% "akka-stream"         % akkaV,
      "com.typesafe.akka" %% "akka-http"           % akkaHttpV,
      "com.typesafe.akka" %% "akka-http-testkit"   % akkaHttpV % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaV % Test,
      "javax.inject"      % "javax.inject"         % "1"
    )

}
