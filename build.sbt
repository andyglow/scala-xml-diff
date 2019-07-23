import xerial.sbt.Sonatype._
import ReleaseTransformations._
import scala.sys.process._


name := "scala-xml-diff"

organization := "com.github.andyglow"

scalaVersion := "2.13.0"

crossScalaVersions := Seq("2.11.12", "2.12.8", "2.13.0")

scalacOptions in (Compile, doc) ++= Opts.doc.title("Scala XML Diff Tool")

scalacOptions in (Compile, doc) ++= Opts.doc.version(version.value)

libraryDependencies ++= Seq(
  "org.scala-lang.modules"  %% "scala-xml" % "1.2.0",
  "org.scalatest"           %% "scalatest" % "3.0.8" % Provided)

scalacOptions ++= {
  val options = Seq(
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
    "-deprecation",
    "-Xfatal-warnings",
    "-Xlint",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Xfuture")

  // WORKAROUND https://github.com/scala/scala/pull/5402
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, 12)) => options.map {
      case "-Xlint"               => "-Xlint:-unused,_"
      case "-Ywarn-unused-import" => "-Ywarn-unused:imports,-patvars,-privates,-locals,-params,-implicits"
      case other                  => other
    }
    case Some((2, n)) if n >= 13  => options.filterNot { opt =>
      opt == "-Yno-adapted-args" || opt == "-Xfuture"
    } :+ "-Xsource:2.13"
    case _             => options
  }
}

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
