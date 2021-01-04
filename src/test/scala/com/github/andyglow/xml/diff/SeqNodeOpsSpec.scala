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

import org.scalatest.OptionValues._
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec._


class SeqNodeOpsSpec extends AnyWordSpec {

  "SeqNodeOps" must {

    "take <x/> and leave <a/><b/></c>" in {
      val (x, xs) = Seq(<a/>, <b/>, <c/>).findAndDrop(_.label == "x")
      x shouldBe Symbol("empty")
      xs shouldBe Seq(<a/>, <b/>, <c/>)
    }

    "take <a/> and leave <b/></c>" in {
      val (x, xs) = Seq(<a/>, <b/>, <c/>).findAndDrop(_.label == "a")
      x shouldBe Symbol("defined")
      x.value shouldBe <a/>
      xs shouldBe Seq(<b/>, <c/>)
    }

    "take <a/> and leave <a/><b/></c>" in {
      val (x, xs) = Seq(<a/>, <a/>, <b/>, <c/>).findAndDrop(_.label == "a")
      x shouldBe Symbol("defined")
      x.value shouldBe <a/>
      xs shouldBe Seq(<a/>, <b/>, <c/>)
    }
  }
}
