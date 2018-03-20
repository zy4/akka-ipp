import ReleaseTransformations._

updateOptions := updateOptions.value.withGigahorse(false)

name := "akka-ipp"
organization := "de.envisia.ipp"
scalaVersion := "2.12.5"
crossScalaVersions := Seq("2.12.5", "2.11.11" ,"2.13.0-M2")
testFrameworks += new TestFramework("utest.runner.Framework")

lazy val root = (project in file("."))
  .settings(
    libraryDependencies ++= Dependencies.commonDeps ++ Seq(scalaOrganization.value % "scala-reflect" % scalaVersion.value)
  )

scalacOptions := Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Xfatal-warnings",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture"
)

// Sonatype Settings for Publishing
developers := List(
  Developer(
    id = "schmitch",
    name = "Christian Schmitt",
    email = "c.schmitt@envisia.de",
    url = url("http://github.com/schmitch")
  ),
  Developer(
    id = "zy4",
    name = "Seung-Zin Nam",
    email = "z.nam@envisia.de",
    url = url("http://github.com/zy4")
  )
)
publishMavenStyle in ThisBuild := true
pomIncludeRepository in ThisBuild := { _ => false }
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}
// Release Settings
releasePublishArtifactsAction := PgpKeys.publishSigned.value
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies, // : ReleaseStep
  inquireVersions, // : ReleaseStep
  runClean, // : ReleaseStep
  runTest, // : ReleaseStep
  setReleaseVersion, // : ReleaseStep
  commitReleaseVersion, // : ReleaseStep, performs the initial git checks
  tagRelease, // : ReleaseStep
  releaseStepCommand(s"""sonatypeOpen "$organization" "envisia-ipp-staging""""),
  publishArtifacts, // : ReleaseStep, checks whether `publishTo` is properly set up
  releaseStepCommand("sonatypeRelease"),
  setNextVersion, // : ReleaseStep
  commitNextVersion, // : ReleaseStep
  pushChanges // : ReleaseStep, also checks that an upstream branch is properly configured
)
