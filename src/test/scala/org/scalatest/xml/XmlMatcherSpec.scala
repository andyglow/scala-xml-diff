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
package org.scalatest.xml

import org.scalatest.wordspec._
import org.scalatest.matchers.should.Matchers._
import org.scalatest.xml.XmlMatchers._

import scala.xml.XML


class XmlMatcherSpec extends AnyWordSpec {

  "XmlMatcher" must {
    "handle simple equality" in {
      <foo/> should beXml(<foo/>)
    }
    "handle simple equality ignoring whitespaces" in {
        <foo> a</foo> should beXml(<foo>a </foo>, ignoreWhitespace = true)
    }
    "handle simple inequality" in {
      <bar/> should not beXml <foo/>
    }
    "handle simple inequality ignoring whitespaces" in {
      <foo> a</foo> should not beXml(<foo>b </foo>, ignoreWhitespace = true)
    }
    "handle simple xml file equality" in {
      val xml1 = XML load classOf[XmlMatcherSpec].getResource("/simple.xml")
      val xml2 = XML load classOf[XmlMatcherSpec].getResource("/simple.xml")
      xml1 should beXml(xml2)
    }
    "handle simple xml file inequality" in {
      val xml1 = XML load classOf[XmlMatcherSpec].getResource("/simple.xml")
      val xml2 = XML load classOf[XmlMatcherSpec].getResource("/simple-with-diff.xml")
      xml1 should not beXml xml2
    }
    "handle normal xml file equality" in {
      val xml1 = XML load classOf[XmlMatcherSpec].getResource("/simple.xml")
      val xml2 = XML load classOf[XmlMatcherSpec].getResource("/simple.xml")
      xml1 should beXml(xml2)
    }
    "handle normal xml file inequality" in {
      val xml1 = XML load classOf[XmlMatcherSpec].getResource("/normal-size.xml")
      val xml2 = XML load classOf[XmlMatcherSpec].getResource("/normal-size-with-diff.xml")
      xml1 should not beXml xml2
    }
  }

}
