import sbt._
import sbt.Keys._
import bintray._
import BintrayKeys._
import scala.language.postfixOps

object ScalaXmlDiffBuild extends Build {

  val projectId = "scala-xml-diff"

  lazy val project = (Project(projectId, file("."))
    settings BuildSettings.settings)

  object Bintray {

    lazy val settings = Seq(
      publishMavenStyle := true,
      publishArtifact in Test := false,
      pomIncludeRepository := { _ => false },
      bintrayReleaseOnPublish in ThisBuild := false,
      licenses += ("GPL-3.0", url("https://www.gnu.org/licenses/gpl-3.0.html")),
      bintrayPackageLabels := Seq("scala", "tools", "xml", "diff"),
      bintrayRepository := "scala-tools",
      homepage := Some(url(s"http://github.com/andyglow/$projectId")),
      checksums := Seq(),
      pomExtra :=
        <scm>
          <url>git://github.com/andyglow/${projectId}.git</url>
          <connection>scm:git://github.com/andyglow/${projectId}.git</connection>
        </scm>
          <developers>
            <developer>
              <id>andyglow</id>
              <name>Andrey Onistchuk</name>
              <url>https://ua.linkedin.com/in/andyglow</url>
            </developer>
          </developers>
    )

  }

  object Dependencies {
    val scalaXml    = "org.scala-lang.modules"  %% "scala-xml" % "1.0.5"  % Compile
    val scalaTest   = "org.scalatest"           %% "scalatest" % "2.2.6"  % Compile
    
    val all = Seq(scalaXml, scalaTest)
  }

  object BuildSettings {

    val ver = "1.1.3"

    lazy val settings = Defaults.coreDefaultSettings ++ Seq(
      version := ver,
      organization := "com.github.andyglow",

      scalaVersion := "2.11.8",

      scalacOptions in Compile ++= Seq("-unchecked", "-deprecation", "-target:jvm-1.8", "-Ywarn-unused-import"),
      scalacOptions in (Compile, doc) ++= Seq("-unchecked", "-deprecation", "-implicits", "-skip-packages", "samples"),
      scalacOptions in (Compile, doc) ++= Opts.doc.title("Scala XML Diff Tool"),
      scalacOptions in (Compile, doc) ++= Opts.doc.version(ver),

      libraryDependencies ++= Dependencies.all

    ) ++ Bintray.settings
  }

}
