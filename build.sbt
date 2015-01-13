name := "scalax-xml-diff"

version := "1.0"

scalaVersion        := "2.11.1"

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
