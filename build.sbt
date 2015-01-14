import bintray.Keys._

name := "scalax-xml-diff"

organization := "com.github.andyglow"

version := "1.1"

scalaVersion        := "2.11.2"

crossScalaVersions  := Seq("2.11.2", "2.10.4")

// add scala-xml dependency when needed (for Scala 2.11 and newer)
// this mechanism supports cross-version publishing
libraryDependencies := {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, scalaMajor)) if scalaMajor >= 11 =>
      libraryDependencies.value :+ "org.scala-lang.modules" %% "scala-xml" % "1.0.1"
    case _ =>
      libraryDependencies.value
  }
}

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"

publishMavenStyle := true

bintrayPublishSettings

repository in bintray := "scala-tools"

licenses := Seq( "LGPL-2.1" -> url( "http://www.gnu.org/licenses/lgpl-2.1.txt" ))

bintrayOrganization in bintray := None