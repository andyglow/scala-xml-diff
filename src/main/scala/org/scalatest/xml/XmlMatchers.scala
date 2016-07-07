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

import com.github.andyglow.xml.diff._
import org.scalatest.matchers._
import org.scalatest.words.ResultOfNotWordForAny

import scala.xml.NodeSeq

trait XmlMatchers {

  class NodeSeqMatcher(e: NodeSeq) extends Matcher[NodeSeq] {
    override def apply(a: NodeSeq): MatchResult = {
      MatchResult(
        (e =?= a).successful,
        s"""$e isn't equal to $a""",
        s"""both xml are equal""")
    }
  }

  // enable not
  implicit class ReflectShouldMatcher[T <: NodeSeq](s: ResultOfNotWordForAny[T]) {
    def beXml(e: NodeSeq) = s be BeMatcher[NodeSeq] { a =>
      new NodeSeqMatcher(e) apply a
    }
  }

  def beXml(e: xml.NodeSeq) = new NodeSeqMatcher(e)

}

object XmlMatchers extends XmlMatchers