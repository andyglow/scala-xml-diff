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
package com.github.andyglow.xml.patch

import com.github.andyglow.xml.diff._
import org.scalatest.{Matchers, WordSpec}

class XmlPatchSpec extends WordSpec with Matchers {

  "XmlPatch" must {
//    "0" in new Scope {
//      lazy val v1 = <foo/>
//      lazy val v2 = <bar/>
//    }
    "1" in new Scope {
      lazy val v1 = <foo k="v" z="34"/>
      lazy val v2 = <bar k="v2" x="x"><i/></bar>
    }

//    "xx" in new Scope {
//
//      lazy val v1 = <invoice>
//        <title>Invoice #4792</title>
//        <id>4792</id>
//        <version>1</version>
//        <line no="0" qty="5" mark="#0976787" price="1"/>
//        <line no="1" qty="2" mark="#0976781" price="2"/>
//        <price>9</price>
//      </invoice>
//
//      lazy val v2 = <in2voice>
//        <title>Invoice #4792</title>
//        <id>4792</id>
//        <version>2</version>
//        <line no="0" qty="6" mark="#0976787" price="1"/>
//        <line no="1" qty="2" mark="#0976781" price="2"/>
//        <line no="2" qty="4" mark="#0976781" price="2"/>
//        <price>18</price>
//      </in2voice>
//
//    }
  }

  trait Scope {
    def v1: xml.NodeSeq
    def v2: xml.NodeSeq

    val mr = v1 =?= v2
    println(mr)
    println(mr.errorMessage)

    val patch = mr.asPatch
    val patched = patch apply v1
    println(v1)
    println(v2)
    println("--")
    println(patched)

    (patched =?= v1) shouldBe (XmlDiff.Eq)

  }

}
