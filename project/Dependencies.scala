import sbt._

object Dependencies {

  def xml(scalaV: ScalaVer): ModuleID = {
    val v = scalaV match {
      case ScalaVer._211 => "1.3.0"
      case ScalaVer._212 => "2.0.0"
      case ScalaVer._213 => "2.0.0"
      case ScalaVer._300 => "2.0.0"
    }

    "org.scala-lang.modules"  %% "scala-xml" % v
  }

  def scalatest = "org.scalatest" %% "scalatest" % "3.2.9" % Provided
}
