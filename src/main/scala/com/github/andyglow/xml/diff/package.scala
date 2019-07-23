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

  implicit class XmlOps[T <: xml.NodeSeq](val e: T) extends AnyVal {

    def =?=(a: T): XmlDiff = {
      XmlDiffComputer.computeMatching(e, a)
    }

    def =#=(a: T)(implicit ev: T <:< xml.Node): XmlDiff = {
      XmlDiffComputer.computeMatching(xml.Utility.trim(e), xml.Utility.trim(a))
    }
  }

  implicit class SeqNodeOps[T <: xml.Node](val nodes: Seq[T]) extends AnyVal {

    def findAndDrop(f: T => Boolean): (Option[T], Seq[T]) = {
      val index = nodes indexWhere f
      if (index < 0) (None, nodes) else {
        val xs = nodes.toList splitAt index match {
          case (left, Nil) => left
          case (left, _ :: right) => left ::: right
        }

        (Some(nodes apply index), xs)
      }
    }

    def elements: Seq[xml.Elem] = nodes collect { case x: xml.Elem => x }

    def noElements: Boolean = elements.isEmpty
  }

  implicit class StringOps(private val x: String) extends AnyVal {

    // scala 2.11 / 2.13 StringOps.lines discrepancy workaround
    def mkLines: Iterator[String] = x.linesWithSeparators map (_.stripLineEnd)
  }
}
