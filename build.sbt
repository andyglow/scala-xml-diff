name := "scala-xml-diff"
organization := "com.github.andyglow"

scalaVersion := "2.11.12"
crossScalaVersions := Seq("2.11.12", "2.12.4")

libraryDependencies ++= Seq(
  "org.scala-lang.modules"  %% "scala-xml" % "1.0.6",
  "org.scalatest"           %% "scalatest" % "3.0.5" % "provided"
)

scalacOptions in Compile ++= Seq("-unchecked", "-deprecation", "-target:jvm-1.8", "-Ywarn-unused-import")
scalacOptions in (Compile, doc) ++= Seq("-unchecked", "-deprecation", "-implicits", "-skip-packages", "samples")
scalacOptions in (Compile, doc) ++= Opts.doc.title("Scala XML Diff Tool")
scalacOptions in (Compile, doc) ++= Opts.doc.version(version.value)

publishMavenStyle := true
publishArtifact in Test := false
pomIncludeRepository := { _ => false }
licenses += ("GPL-3.0", url("https://www.gnu.org/licenses/gpl-3.0.html"))
licenses += ("LGPL-3.0", url("https://www.gnu.org/licenses/lgpl-3.0.html"))
bintrayPackageLabels := Seq("scala", "tools", "xml", "diff")
bintrayRepository := "scala-tools"
homepage := Some(url("http://github.com/andyglow/scala-xml-diff"))
checksums := Seq()
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
