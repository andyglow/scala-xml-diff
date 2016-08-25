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

private[diff] object XmlDiffComputer {

  def matchNames(e: xml.Node, a: xml.Node): XmlDiff = {
    def testName = if (e.label == a.label) None else Some(UnequalName(e.label, a.label))
    def testNsUri = if (e.namespace == a.namespace) None else Some(UnequalNamespaceUri(e.namespace, a.namespace))
    List(testName, testNsUri).flatten match {
      case Nil => Eq
      case details => Neq(details)
    }
  }

  def matchAttributes(e: xml.Elem, a: xml.Elem): XmlDiff = {
    def contains(leftElem: xml.Elem, leftMeta: xml.MetaData, rightElem: xml.Elem, rightMeta: xml.MetaData)(v: (Option[String], String, String) => XmlDiff): XmlDiff = {
      val lookup = if (leftMeta.isPrefixed) {
        val uri = leftMeta getNamespace leftElem
        rightMeta.get(uri, rightElem.scope, leftMeta.key)
      } else
        rightMeta get leftMeta.key

      val res = v(lookup map (_.text), leftMeta.key, leftMeta.value.text)
      if (leftMeta.hasNext) res ++ contains(leftElem, leftMeta.next, rightElem, rightMeta)(v) else res
    }

    (e.attributes, a.attributes) match {
      case (xml.Null, xml.Null) => Eq
      case (xml.Null, right) =>
        val redundant = right.asAttrMap map { case (k, v) => RedundantAttribute(k, v) }
        Neq(redundant.toList)
      case (left, xml.Null) =>
        val absent = left.asAttrMap map { case (k, v) => AbsentAttribute(k, v) }
        Neq(absent.toList)
      case (left, right) =>
        val leftToRight = contains(e, left, a, right) {
          case (Some(right), _, left) if left == right => Eq
          case (Some(right), k, left) => Neq(UnequalAttribute(k, left, right))
          case (None, k, left) => Neq(AbsentAttribute(k, left))
        }
        val rightToLeft = contains(a, right, e, left) {
          case (Some(_), _, _) => Eq
          case (None, k, right) => Neq(RedundantAttribute(k, right))
        }

        leftToRight ++ rightToLeft
    }

  }

  private def compute(left: Seq[xml.Node], right: Seq[xml.Node])(v: (Option[xml.Node], xml.Node) => XmlDiff): (XmlDiff, Seq[xml.Node]) = {
    def contains(one: xml.Node, all: Seq[xml.Node]): (XmlDiff, Seq[xml.Node]) = {
      val lookup = Lookup(one)
      val (head, tail) = all findAndDrop lookup.apply

      val childrenRes = head map { x =>
        matchChildren(one.child, x.child) flatMap { details =>
          List(UnequalElem(one.label, details))
        }
      } getOrElse Eq


      (v(head, one) ++ childrenRes, tail)
    }

    if (left.size == 1) {
      contains(left.head, right)
    } else if (left.size > 1) {
      val (diff1, rest1) = contains(left.head, right)
      val (diff2, rest2) = compute(left.tail, rest1)(v)
      (diff1 ++ diff2, rest2)
    } else {
      (Eq, Seq.empty)
    }
  }

  def matchChildren(e: Seq[xml.Node], a: Seq[xml.Node]): XmlDiff = {
    val (diff1, rest) = compute(e, a) {
      case (Some(that: xml.Elem), one: xml.Elem) =>
        matchAttributes(one, that) flatMap { details =>
          List(UnequalElem(one.label, details))
        }

      case (Some(that), one) =>
        if (one.text.trim == that.text.trim) Eq else Neq(AbsentNode(one), RedundantNode(that))

      case (None, one) => Neq(AbsentNode(one))
    }

    val diff2 = rest.foldLeft[XmlDiff](Eq) {
      case (diff, that) => diff ++ Neq(RedundantNode(that))
    }

    diff1 ++ diff2
  }

  def computeMatching(e: xml.NodeSeq, a: xml.NodeSeq): XmlDiff = {
    (e, a) match {
      case (e: xml.Elem, a: xml.Elem) =>
        matchNames(e, a) ++
        matchAttributes(e, a) ++
        matchChildren(e.child, a.child) flatMap {
          details => List(UnequalElem(e.label, details))
        }

      case (e: xml.Node, a: xml.Node) =>
        if (e.text.trim == a.text.trim) Eq else
          Neq(AbsentNode(e), RedundantNode(a))

      case _ =>
        matchChildren(e.theSeq, a.theSeq)

    }
  }
}
