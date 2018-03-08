name := "scala-xml-diff"

organization := "com.github.andyglow"

scalaVersion := "2.11.12"

crossScalaVersions := Seq("2.11.12", "2.12.4")

scalacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture")

scalacOptions in (Compile,doc) ++= Seq(
  "-groups",
  "-implicits",
  "-no-link-warnings")

scalacOptions in (Compile, doc) ++= Opts.doc.title("Scala XML Diff Tool")

scalacOptions in (Compile, doc) ++= Opts.doc.version(version.value)

licenses ++= Seq(
  ("GPL-3.0", url("https://www.gnu.org/licenses/gpl-3.0.html")),
  ("LGPL-3.0", url("https://www.gnu.org/licenses/lgpl-3.0.html")))

bintrayPackageLabels := Seq("scala", "tools", "xml", "diff")

bintrayRepository := "scala-tools"

bintrayOrganization := Some("andyglow")

resolvers += Resolver.bintrayRepo("andyglow", "scala-tools")

homepage := Some(url("http://github.com/andyglow/scala-xml-diff"))

releaseCrossBuild := true

pomExtra :=
  <scm>
    <url>git://github.com/andyglow/scala-xml-diff.git</url>
    <connection>scm:git://github.com/andyglow/scala-xml-diff.git</connection>
  </scm>
  <developers>
    <developer>
      <id>andyglow</id>
      <name>Andrey Onistchuk</name>
      <url>https://ua.linkedin.com/in/andyglow</url>
    </developer>
  </developers>

libraryDependencies ++= Seq(
  "org.scala-lang.modules"  %% "scala-xml" % "1.0.6",
  "org.scalatest"           %% "scalatest" % "3.0.5" % "provided"
)
