package com.github.andyglow.xml.diff

import com.github.andyglow.xml.diff.XmlPath._
import com.github.andyglow.xml.diff.XmlPath.NameMatcher._
import org.scalatest.{Matchers, FlatSpec}

import scala.language.implicitConversions

/**
  *
  * @author Andrey Onistchuk
  */
class XmlPathSpec extends FlatSpec with Matchers {

  "'a/x/*'" must "be parsed and formated back in the same way" in {
    assert { XmlPath("a/x/*").toString == "a/x/*"}
  }

  "'a/b/c'" must "be parsed as a/b/c xml path" in {
    XmlPath("a/b/c") shouldBe (Text("a") :: Text("b") :: Text("c") :: Nil)
  }

  "'a/*'" must "be parsed as a/* xml path" in {
    XmlPath("a/*") shouldBe (Text("a") :: Wildcard :: Nil)
  }

  "*" must "match x/y/z" in {
    assert { XmlPath("*") matches "a/b/c" }
  }

  "*" must "match a/b/c" in {
    assert { XmlPath("*") matches "a/b/c" }
  }

  "a/b/c" must "match a/b/c" in {
    assert { XmlPath("a/b/c") matches "a/b/c" }
  }

  "a/*" must "match a/b/c" in {
    assert { XmlPath("a/*") matches "a/b/c" }
  }

  "a/*" must "match a/d/e" in {
    assert { XmlPath("a/*") matches "a/d/e" }
  }

  "a/*/z" must "match a/_/z" in {
    assert { XmlPath("a/*/z") matches "a/_/z" }
  }

  "a/*/z" must "match a/_/n/o/a/qqq/z" in {
    assert { XmlPath("a/*/z") matches "a/_/n/o/a/qqq/z" }
  }

  "a/*/c/*/e" must "match a/b/c/d/e" in {
    assert { XmlPath("a/*/c/*/e") matches "a/b/c/d/e" }
  }

  "a/*/c/*/e" must "match a/a/b/c/x/y/z/e" in {
    assert { XmlPath("a/*/c/*/e") matches "a/a/b/c/x/y/z/e" }
  }

  private implicit def parseList(text: String): List[String] = text.split("/").toList

}
