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

import org.scalatest._

import scala.xml.{UnprefixedAttribute => Attr}

class XmlComparatorSpec extends FlatSpec with Matchers {

  "<foo/>" must "be <foo/>" in {
    val x1 = <foo/>
    val x2 = <foo/>
    (x1 compareTo x2) should be(NoDiff)
  }

  "<foo key=\"val\"/>" must "be <foo key=\"val\"/>" in {
    val x1 = <foo key="val"/>
    val x2 = <foo key="val"/>
    (x1 compareTo x2) should be(NoDiff)
  }

  "<foo><bar key=\"val\"/></foo>" must "be <foo><bar key=\"val\"/></foo>" in {
    val x1 = <foo><bar key="val"/></foo>
    val x2 = <foo><bar key="val"/></foo>
    (x1 compareTo x2) should be(NoDiff)
  }

  "<foo key=\"val1\"/>" must " not be <foo key=\"val2\"/>" in {
    val x1 = <foo key="val1"/>
    val x2 = <foo key="val2"/>
    x1 compareTo x2 match {
      case diff@AttributesDiff(_,
        xml.UnprefixedAttribute("key", Seq(xml.Text("val1")), xml.Null),
        xml.UnprefixedAttribute("key", Seq(xml.Text("val2")), xml.Null)) => assert(true)
      case _ => assert(false)
    }
  }

  "<foo key1=\"val1\" key2=\"val2\"/>" must " not be <foo key1=\"val1\"/>" in {
    val x1 = <foo key1="val1" key2="val2"/>
    val x2 = <foo key1="val1"/>
    x1 compareTo x2 match {
      case diff@AttributesDiff(_,
        Attr("key1", Seq(xml.Text("val1")), Attr("key2", Seq(xml.Text("val2")), xml.Null)),
        Attr("key1", Seq(xml.Text("val1")), xml.Null)) => assert(true)
      case _ => assert(false)
    }
  }

  "<foo><bar/></foo>" must " not be <foo><baz/></foo>" in {
    val x1 = <foo><bar/></foo>
    val x2 = <foo><baz/></foo>
    x1 compareTo x2 match {
      case diff@NodeNotFound(_, <bar/>) => assert(true)
      case diff@_ => assert(false)
    }
  }

  "<foo><bar key=\"val1\"/></foo>" must " not be <foo><bar key=\"val2\"/></foo>" in {
    val x1 = <foo><bar key="val1"/></foo>
    val x2 = <foo><bar key="val2"/></foo>
    x1 compareTo x2 match {
      case diff@ChildrenDiff(_, _, List(AttributesDiff(_,
        Attr("key", Seq(xml.Text("val1")), xml.Null),
        Attr("key", Seq(xml.Text("val2")), xml.Null)))) => assert(true)
      case diff@_ => assert(false)
    }
  }

  "<foo><bar key1=\"val1\" key2=\"val2\"/></foo>" must " not be <foo><bar key1=\"val1\"/></foo>" in {
    val x1 = <foo><bar key1="val1" key2="val2"/></foo>
    val x2 = <foo><bar key1="val1"/></foo>
    x1 compareTo x2 match {
      case diff@ChildrenDiff(_, _, List(AttributesDiff(_,
      Attr("key1", Seq(xml.Text("val1")), Attr("key2", Seq(xml.Text("val2")), xml.Null)),
      Attr("key1", Seq(xml.Text("val1")), xml.Null)))) => assert(true)
      case diff@_ => assert(false)
    }
  }

  "<foo><bar key1=\"val1\"/></foo>" must " not be (strictly) <foo><bar key1=\"val1\" key2=\"val2\"/></foo>" in {
    val x1 = <foo><bar key1="val1"/></foo>
    val x2 = <foo><bar key1="val1" key2="val2"/></foo>
    XmlComparator(strict = true).compare(x1, x2) match {
      case diff@ChildrenDiff(_, _, List(AttributesDiff(_,
        Attr("key1", Seq(xml.Text("val1")), xml.Null),
        Attr("key1", Seq(xml.Text("val1")), Attr("key2", Seq(xml.Text("val2")), xml.Null))))) => assert(true)
      case diff@_ => assert(false)
    }
  }


}
