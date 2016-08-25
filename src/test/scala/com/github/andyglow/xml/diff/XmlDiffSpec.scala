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

import com.github.andyglow.xml.diff.XmlDiff._
import org.scalatest.MustMatchers._
import org.scalatest._

class XmlDiffSpec extends WordSpec with Matchers {

  "XmlDiff" must {

    "match the same simple empty elements" in {
      (<foo/> =?= <foo/>) mustBe (Eq)
    }

    "not match different simple empty elements" in {
      (<foo/> =?= <bar/>) mustBe (Neq(UnequalElem("foo", List(UnequalName("foo", "bar")))))
    }

    "not match same named simple empty elements in different namespaces" in {
      (<x:foo xmlns:x="http://xxx"/> =?= <x:foo xmlns:x="http://yyy"/>) mustBe (
        Neq(UnequalElem("foo", List(UnequalNamespaceUri("http://xxx", "http://yyy")))))
    }

    "match the same simple elements with text" in {
      (<foo>xxx</foo> =?= <foo>xxx</foo>) mustBe (Eq)
    }

    "match the same simple elements with text, whitespaces are ignored" in {
      val xml1 =
        <root>
          <fee>ttt</fee> <foo>xxx</foo> <bar>rrr</bar>
        </root>

      val xml2 =
        <root>
          <fee>ttt
          </fee>
          <foo>
            xxx

          </foo> <bar>rrr</bar>
        </root>

      (xml1 =#= xml2) mustBe (Eq)
    }

    "not match the same simple elements with different text" in {
      (<foo>xxx</foo> =?= <foo>yyy</foo>) mustBe (Neq(UnequalElem("foo", List(
        AbsentNode(scala.xml.Text("xxx")),
        RedundantNode(scala.xml.Text("yyy"))))))
    }

    "match the same empty elements with the same attributes" in {
      (<foo key="val"/> =?= <foo key="val"/>) mustBe (Eq)
    }

    "not match the same empty elements with different attribute value" in {
      (<foo key="val1"/> =?= <foo key="val2"/>) mustBe (
        Neq(UnequalElem("foo", List(UnequalAttribute("key", "val1", "val2")))))
    }

    "not match the same empty elements with different attribute set" in {
      (<foo key1="val1"/> =?= <foo key2="val2"/>) mustBe (
        Neq(UnequalElem("foo", List(AbsentAttribute("key1", "val1"), RedundantAttribute("key2", "val2")))))
    }

    "match the same elements with the same childen" in {
      (<foo><bar/></foo> =?= <foo><bar/></foo>) mustBe (Eq)
      (<foo><bar/><baz/></foo> =?= <foo><bar/><baz/></foo>) mustBe (Eq)
      (<foo><bar key="val"/></foo> =?= <foo><bar key="val"/></foo>) mustBe (Eq)
    }

    "not match the same elements with different childern" in {
      (<foo><bar/></foo> =?= <foo><baz/></foo>) mustBe (
        Neq(UnequalElem("foo", List(AbsentNode(<bar/>), RedundantNode(<baz/>)))))
    }

    "not match the same inner element with different arguments" in {
      (<foo><bar key="val1" key2="val2"/></foo> =?= <foo><bar key="val2" key3="val3"/></foo>) mustBe (
        Neq(UnequalElem("foo", List(UnequalElem("bar", List(
          UnequalAttribute("key", "val1", "val2"),
          AbsentAttribute("key2", "val2"),
          RedundantAttribute("key3", "val3")))))))
    }

    "not match the same inner element with different children" in {
      (<foo><bar><baz/></bar><baz><bar/></baz></foo> =?= <foo><bar><bax/></bar><baz><bax/></baz></foo>) mustBe (
        Neq(UnequalElem("foo", List(
          UnequalElem("bar", List(AbsentNode(<baz/>), RedundantNode(<bax/>))),
          UnequalElem("baz", List(AbsentNode(<bar/>), RedundantNode(<bax/>)))))))
    }

  }

}
