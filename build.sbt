import ReleaseTransformations._

ThisBuild / name := "Schnéileopard"
ThisBuild / organization := "io.github.olaflaitinen"
ThisBuild / scalaVersion := "3.6.1"
ThisBuild / crossScalaVersions := Seq("3.6.1")

ThisBuild / versionScheme := Some("semver-spec")
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / homepage := Some(url("https://github.com/olaflaitinen/schneileopard"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/olaflaitinen/schneileopard"),
    "scm:git@github.com:olaflaitinen/schneileopard.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id = "olaf.laitinen",
    name = "Gustav Olaf Yunus Laitinen-Fredriksson Lundstrom-Imanov",
    email = "olaf.laitinen@uni.lu",
    url = url("https://www.uni.lu/")
  )
)

ThisBuild / licenses := Seq("EUPL-1.2" -> url("https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12"))

ThisBuild / startYear := Some(2026)

ThisBuild / pomIncludeRepository := { _ => false }

ThisBuild / publishTo := sonatypePublishToBundle.value

ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / sonatypeRepository := "https://s01.oss.sonatype.org/service/local"

ThisBuild / releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  setNextVersion,
  commitNextVersion,
  pushChanges,
  releaseStepCommandAndRemaining("+publishSigned"),
  releaseStepCommand("sonatypeBundleRelease")
)

ThisBuild / scalafmtOnCompile := true

val scala3Settings = Seq(
  scalacOptions ++= Seq(
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
    "-deprecation",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-Ysafe-init",
    "-source:3.1"
  )
)

val testDeps = Seq(
  "org.scalatest" %% "scalatest" % "3.2.18" % Test,
  "org.scalatestplus" %% "scalacheck-1-17" % "3.2.18.0" % Test,
  "org.scalacheck" %% "scalacheck" % "1.17.1" % Test
)

val csvDeps = Seq(
  "com.github.tototoshi" %% "scala-csv" % "1.4.1"
)

lazy val root = project
  .in(file("."))
  .aggregate(
    core,
    omics,
    graph,
    ai,
    io,
    examples,
    bench
  )
  .settings(
    publish / skip := true,
    publishArtifact := false
  )

lazy val core = project
  .in(file("modules/core"))
  .settings(
    name := "schneileopard-core",
    scala3Settings,
    libraryDependencies ++= testDeps
  )

lazy val omics = project
  .in(file("modules/omics"))
  .dependsOn(core % "test->test;compile->compile")
  .settings(
    name := "schneileopard-omics",
    scala3Settings,
    libraryDependencies ++= testDeps
  )

lazy val graph = project
  .in(file("modules/graph"))
  .dependsOn(core % "test->test;compile->compile")
  .settings(
    name := "schneileopard-graph",
    scala3Settings,
    libraryDependencies ++= testDeps
  )

lazy val ai = project
  .in(file("modules/ai"))
  .dependsOn(
    core % "test->test;compile->compile",
    omics % "test->test;compile->compile",
    graph % "test->test;compile->compile"
  )
  .settings(
    name := "schneileopard-ai",
    scala3Settings,
    libraryDependencies ++= testDeps
  )

lazy val io = project
  .in(file("modules/io"))
  .dependsOn(
    core % "test->test;compile->compile",
    omics % "test->test;compile->compile"
  )
  .settings(
    name := "schneileopard-io",
    scala3Settings,
    libraryDependencies ++= testDeps ++ csvDeps
  )

lazy val examples = project
  .in(file("modules/examples"))
  .dependsOn(
    core,
    omics,
    graph,
    ai,
    io
  )
  .settings(
    name := "schneileopard-examples",
    scala3Settings,
    libraryDependencies ++= testDeps,
    publish / skip := true,
    publishArtifact := false
  )

lazy val bench = project
  .in(file("modules/bench"))
  .dependsOn(
    core,
    omics,
    graph,
    ai,
    io
  )
  .settings(
    name := "schneileopard-bench",
    scala3Settings,
    publish / skip := true,
    publishArtifact := false,
    libraryDependencies ++= Seq(
      "org.openjdk.jmh" % "jmh-core" % "1.37",
      "org.openjdk.jmh" % "jmh-generator-annprocess" % "1.37" % Compile
    )
  )
