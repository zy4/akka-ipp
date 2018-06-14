updateOptions := updateOptions.value.withGigahorse(false)

// Plublishing
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.0")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.1")

// Release
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.7")

// Scala Formatting
addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "1.4.0")
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")

// Coverage
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
