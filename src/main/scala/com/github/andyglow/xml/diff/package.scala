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
package com.github.andyglow.xml

package object diff {

  implicit class XmlOps(val e: xml.NodeSeq) extends AnyVal {
    def =?=(a: xml.NodeSeq): XmlDiff = XmlDiffComputer.computeMatching(e, a)
  }

  implicit class SeqNodeOps[T <: xml.Node](val nodes: Seq[T]) extends AnyVal {
    def findAndDrop(f: T => Boolean): (Option[T], Seq[T]) = {
      val index = nodes indexWhere f
      if (index < 0) {
        (None, nodes)
      } else {
        val xs = (nodes.toList splitAt index) match {
          case (left, Nil) => left
          case (left, _ :: right) => left ::: right
        }
        (Some(nodes apply index), xs)
      }
    }
    def elements: Seq[xml.Elem] = nodes collect {
      case x: xml.Elem => x
    }
    def noElements: Boolean = elements.isEmpty
  }

  sealed trait XmlDiff {
    def successful: Boolean
    def ++(that: XmlDiff): XmlDiff
    def flatMap(f: List[XmlDiff.Detail] => List[XmlDiff.Detail]): XmlDiff = this match {
      case XmlDiff.Eq => XmlDiff.Eq
      case XmlDiff.Neq(details) => XmlDiff.Neq(f(details))
    }
  }

  object XmlDiff {
    case object Eq extends XmlDiff {
      def successful: Boolean = true
      def ++(that: XmlDiff): XmlDiff = that
    }
    case class Neq(details: List[XmlDiff.Detail]) extends XmlDiff {
      def successful: Boolean = false
      def ++(that: XmlDiff): XmlDiff = that match {
        case Eq => this
        case Neq(thatDetails) => Neq(details ++ thatDetails)
      }
    }

    sealed trait Detail
    case class UnequalName(expected: String, actual: String) extends Detail
    case class UnequalNamespaceUri(expected: String, actual: String) extends Detail
    case class UnequalText(expected: String, actual: String) extends Detail
    case class UnequalAttribute(name: String, expected: String, actual: String) extends Detail
    case class AbsentAttribute(name: String, value: String) extends Detail
    case class RedundantAttribute(name: String, value: String) extends Detail
    case class UnequalElem(label: String, details: List[Detail]) extends Detail
    case class AbsentElem(elem: xml.Elem) extends Detail
    case class RedundantElem(elem: xml.Elem) extends Detail

    object Neq {
      def apply(head: XmlDiff.Detail, tail: XmlDiff.Detail*): Neq = Neq(head :: tail.toList)
    }
  }


  private[diff] case class NonEmptyList[T](head: T, tail: List[T]) {
    def toList: List[T] = head :: tail
  }

}
