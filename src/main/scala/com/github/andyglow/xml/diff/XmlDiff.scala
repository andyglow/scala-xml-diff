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

import com.github.andyglow.xml.patch.XmlPatch


sealed trait XmlDiff {

  def successful: Boolean

  def ++(that: XmlDiff): XmlDiff

  def flatMap(f: List[XmlDiff.Detail] => List[XmlDiff.Detail]): XmlDiff = this match {
    case XmlDiff.Eq           => XmlDiff.Eq
    case XmlDiff.Neq(details) => XmlDiff.Neq(f(details))
  }

  def asPatch: XmlPatch

  def errorMessage: String
}

object XmlDiff {

  case object Eq extends XmlDiff {

    def successful: Boolean = true

    def ++(that: XmlDiff): XmlDiff = that

    lazy val asPatch: XmlPatch = XmlPatch.empty

    def errorMessage: String = ""
  }

  case class Neq(details: List[XmlDiff.Detail]) extends XmlDiff {

    def successful: Boolean = false

    def ++(that: XmlDiff): XmlDiff = that match {
      case Eq => this
      case Neq(thatDetails) => Neq(details ++ thatDetails)
    }

    def errorMessage: String = details map (_.errorMessage) mkString "\n"

    lazy val asPatch: XmlPatch = XmlPatch(details)
  }

  sealed trait Detail {

    def errorMessage: String
  }

  sealed trait NodeDetail extends Detail

  case class UnequalElem(label: String, details: List[Detail]) extends NodeDetail {

    def errorMessage: String = {
      val name = details collectFirst { case diff: UnequalName => diff.errorMessage } getOrElse label
      val attrNe = details collect { case diff: UnequalAttribute => diff.errorMessage }
      val attrAbs = details collect { case diff: AbsentAttribute => diff.errorMessage }
      val attrRdn = details collect { case diff: RedundantAttribute => diff.errorMessage }
      val nodes = details collect { case diff: NodeDetail => diff.errorMessage }

      val sb = new StringBuilder
      sb.append("<").append(name)
      for (a <- attrNe) sb.append(" ").append(a)
      for (a <- attrAbs) sb.append(" ").append(a)
      for (a <- attrRdn) sb.append(" ").append(a)
      sb.append(">\n")
      for (n <- nodes) {
        val lines = n.mkLines map (line => s"  $line") mkString "\n"
        sb.append(lines)
      }
      sb.append("</").append(name).append(">")
      sb.toString
    }
  }

  case class UnequalName(expected: String, actual: String) extends Detail {
    require(expected != actual)

    def errorMessage: String = {
      s"-[$actual]+[$expected]"
    }
  }

  case class UnequalNamespaceUri(expected: String, actual: String) extends Detail {

    def errorMessage: String = ""
  }

  case class UnequalAttribute(name: String, expected: String, actual: String) extends Detail {
    require(expected != actual)

    def errorMessage: String = s"""$name=-[$actual]+[$expected]"""

  }
  case class AbsentAttribute(name: String, value: String) extends Detail {

    def errorMessage: String = s"""+[$name=$value]"""
  }

  case class RedundantAttribute(name: String, value: String) extends Detail {

    def errorMessage: String = s"""-[$name=$value]"""
  }

  case class AbsentNode(elem: xml.Node) extends NodeDetail {

    def errorMessage: String = s"absent $elem"
  }

  case class RedundantNode(elem: xml.Node) extends NodeDetail {

    def errorMessage: String = s"redundant $elem"
  }

  object Neq {
    def apply(head: XmlDiff.Detail, tail: XmlDiff.Detail*): Neq = Neq(head :: tail.toList)
  }
}
