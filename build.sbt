import xerial.sbt.Sonatype._
import ReleaseTransformations._
import scala.sys.process._
import ScalaVer._

name := "scala-xml-diff"

organization := "com.github.andyglow"

scalaVersion := (ScalaVer.fromEnv getOrElse ScalaVer.default).full

crossScalaVersions := ScalaVer.values.map(_.full)

scalaV := ScalaVer.fromString(scalaVersion.value) getOrElse ScalaVer.default

scalacOptions := CompilerOptions(scalaV.value)

Compile / doc / scalacOptions ++= Opts.doc.title("Scala XML Diff Tool")

Compile / doc / scalacOptions ++= Opts.doc.version(version.value)

libraryDependencies ++= Seq(
  Dependencies.xml(scalaV.value),
  Dependencies.scalatest)

// release
publishTo := sonatypePublishTo.value

licenses ++= Seq(
  ("GPL-3.0", url("https://www.gnu.org/licenses/gpl-3.0.html")),
  ("LGPL-3.0", url("https://www.gnu.org/licenses/lgpl-3.0.html")))

homepage := Some(url("http://github.com/andyglow/scala-xml-diff"))

startYear := Some(2017)

organizationName := "andyglow"

releaseCrossBuild := true

sonatypeProfileName := "com.github.andyglow"

publishMavenStyle := true

sonatypeProjectHosting := Some(
  GitHubHosting(
    "andyglow",
    "scala-xml-diff",
    "andyglow@gmail.com"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/andyglow/scala-xml-diff"),
    "scm:git@github.com:andyglow/scala-xml-diff.git"))

developers := List(
  Developer(
    id    = "andyglow",
    name  = "Andriy Onyshchuk",
    email = "andyglow@gmail.com",
    url   = url("https://ua.linkedin.com/in/andyglow")))


releasePublishArtifactsAction := PgpKeys.publishSigned.value

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep(action = Command.process("publishSigned", _), enableCrossBuild = true),
  setNextVersion,
  commitNextVersion,
  ReleaseStep(action = Command.process("sonatypeReleaseAll", _), enableCrossBuild = true),
  pushChanges)
