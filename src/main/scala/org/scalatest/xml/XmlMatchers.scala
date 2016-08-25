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

trait XmlMatchers {

  class NodeMatcher(e: xml.Node, ignoreWhitespace: Boolean) extends Matcher[xml.Node] {
    override def apply(a: xml.Node): MatchResult = {
      val res = if(ignoreWhitespace) (e =#= a).successful else (e =?= a).successful
      MatchResult(
        res,
        s"""$e isn't equal to $a""",
        s"""both xml are equal""")
    }
  }

  // enable not
  implicit class ReflectShouldNodeMatcher[T <: xml.Node](s: ResultOfNotWordForAny[T]) {
    def beXml(e: xml.Node, ignoreWhitespace: Boolean = false) = s be BeMatcher[xml.Node] { a =>
      new NodeMatcher(e, ignoreWhitespace) apply a
    }
  }


  def beXml(e: xml.Node, ignoreWhitespace: Boolean = false) = new NodeMatcher(e, ignoreWhitespace)

}

object XmlMatchers extends XmlMatchers