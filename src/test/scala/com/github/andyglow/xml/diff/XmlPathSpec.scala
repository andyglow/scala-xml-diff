/**
  * scala-xml-diff
  * Copyright (c) 2014, Andrey Onistchuk, All rights reserved.
  *
  * This library is free software; you can redistribute it and/or
  * modify it under the terms of the GNU Lesser General Public
  * License as published by the Free Software Foundation; either
  * version 3.0 of the License, or (at your option) any later version.
  *
  * This library is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  * Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public
  * License along with this library.
  */
package com.github.andyglow.xml.diff

import com.github.andyglow.xml.diff.XmlPath._
import com.github.andyglow.xml.diff.XmlPath.NameMatcher._
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec._

import scala.language.implicitConversions


class XmlPathSpec extends AnyWordSpec {

  "XmlPath" must {
    "parse 'a/x/*' and format it back in the same way" in {
      assert { XmlPath("a/x/*").toString == "a/x/*"}
    }
    "parse 'a/b/c' as a/b/c xml path" in {
      XmlPath("a/b/c") shouldBe (Text("a") :: Text("b") :: Text("c") :: Nil)
    }
    "parse 'a/*' as a/* xml path" in {
      XmlPath("a/*") shouldBe (Text("a") :: Wildcard :: Nil)
    }
    "match '*' against x/y/z" in {
      assert { XmlPath("*") matches "x/y/z" }
    }
    "match 'a/b/c' against a/b/c" in {
      assert { XmlPath("a/b/c") matches "a/b/c" }
    }
    "match 'a/*' against a/b/c" in {
      assert { XmlPath("a/*") matches "a/b/c" }
    }
    "match 'a/*' against a/d/e" in {
      assert { XmlPath("a/*") matches "a/d/e" }
    }
    "match 'a/*/z' against a/_/z" in {
      assert { XmlPath("a/*/z") matches "a/_/z" }
    }
    "match 'a/*/z' against a/_/n/o/a/qqq/z" in {
      assert { XmlPath("a/*/z") matches "a/_/n/o/a/qqq/z" }
    }
    "match 'a/*/c/*/e' against a/b/c/d/e" in {
      assert { XmlPath("a/*/c/*/e") matches "a/b/c/d/e" }
    }
    "match 'a/*/c/*/e' against a/a/b/c/x/y/z/e" in {
      assert { XmlPath("a/*/c/*/e") matches "a/a/b/c/x/y/z/e" }
    }
  }

  private implicit def parseList(text: String): List[String] = text.split("/").toList

}
