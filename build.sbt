import sbt._

lazy val comonad = (project in file(".")).
  settings (
    name := "comonad",
    organization := "justinhj",
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.13.1"
    // add other settings here
  )

/* scala versions and options */
scalaVersion := "2.13.1"

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full)

enablePlugins(JmhPlugin)
// jmh:run -t 1 -f 1 -wi 5 -i 5 .*Bench.*

// These options will be used for *all* versions.
scalacOptions ++= Seq(
  "-deprecation"
  , "-unchecked"
  , "-encoding", "UTF-8"
  , "-Xlint"
  , "-Xverify"
  , "-feature"
  //,"-Xfatal-warnings"
  , "-language:_"
  //,"-optimise"
)

javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation", "-source", "1.7", "-target", "1.7")

val CatsVersion = "2.1.1"
val AmmoniteVersion = "2.0.0"

libraryDependencies ++= Seq(
  // -- testing --
  "org.scalacheck" %% "scalacheck" % "1.14.3" % "test",
  "com.github.alexarchambault" %% "scalacheck-shapeless_1.14" % "1.2.3" % "test",
  "org.scalatest" %% "scalatest" % "3.1.1" % "test",
  // Cats
  "org.typelevel" %% "cats-core" % CatsVersion,
  "org.typelevel" %% "cats-laws" % CatsVersion,
  "org.typelevel" %% "cats-testkit-scalatest" % "1.0.1",
  // type classes
  "com.github.mpilquist" %% "simulacrum" % "0.19.0",
  // li haoyi ammonite repl embed
  "com.lihaoyi" % "ammonite" % AmmoniteVersion % "test" cross CrossVersion.full
)

resolvers ++= Seq(
  "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
  "Secured Central Repository" at "https://repo1.maven.org/maven2",
  Resolver.sonatypeRepo("snapshots")
)

// ammonite repl
sourceGenerators in Test += Def.task {
  val file = (sourceManaged in Test).value / "amm.scala"
  IO.write(file, """object amm extends App { ammonite.Main().run() }""")
  Seq(file)
}.taskValue
