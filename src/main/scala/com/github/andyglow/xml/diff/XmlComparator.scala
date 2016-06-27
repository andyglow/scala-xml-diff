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

import XmlDiff._

case class XmlComparisonContext(path: List[xml.Node] = Nil) {
  def append(node: xml.Node): XmlComparisonContext = this.copy(path = node :: path)
}

object XmlComparator {
  def apply(
    ignorePaths: List[XmlPath] = Nil,
    ignoreTextDiffs: Boolean = false,
    strict: Boolean = false): XmlComparator = {

    new XmlComparator(ignorePaths, ignoreTextDiffs, strict)
  }
}

class XmlComparator(
  val ignorePaths: List[XmlPath] = Nil,
  val ignoreTextDiffs: Boolean = false,
  val strict: Boolean = false) {

  def shouldSkip(context: XmlComparisonContext, e: xml.Node): Boolean = {
    val ps = e.label :: (context.path map (_.label))
    ignorePaths.exists(_.matches(ps.reverse))
  }

  private def compareChildren(
    context: XmlComparisonContext,
    expected: List[xml.Node],
    actual: List[xml.Node]): XmlDiffResult = {

    expected match {
      case e :: tail =>
        e match {
          case e: xml.Elem if !shouldSkip(context, e) => findMatchingNode(context, tail, actual, e)
          case xml.Text(_t) if _t.trim.isEmpty => XmlEqual
          case _ => compare(e, actual.head, context)
        }
      case Nil => XmlEqual
    }
  }

  private def findMatchingNode(
    context: XmlComparisonContext,
    expected: List[xml.Node],
    actual: List[xml.Node],
    e: xml.Node): XmlDiffResult = {

    val comparison = actual.filter {
      case a: xml.Node => isNodeNamesEqual(context, e, a)
      case _ => false
    } map { n => (n, compare(e, n, context)) }

    if (comparison.isEmpty)
      XmlDifferent(AbsentNode(context.path, e))
    else {
      def diffs = comparison collect { case (_, XmlDifferent(diff)) => diff }
      val hasEquals = comparison collectFirst { case (_, XmlEqual) => true } getOrElse false
      if (hasEquals) {
        compareChildren(context, expected, actual.dropWhile(n => comparison.exists { case (node, _) => node == n }))
      } else {
        XmlDifferent(ChildrenDiff(context.path, e, diffs))
      }
    }
  }

  def isNodeNamesEqual(context: XmlComparisonContext, e: xml.Node, a: xml.Node): Boolean = (
    shouldSkip(context, e)
    || (e.label == a.label
        && e.scope.getURI(e.prefix) == a.scope.getURI(a.prefix)
      )
    )

  private def isAttrsEqual(e1: xml.Elem, e2: xml.Elem): Boolean = {
    val a1 = e1.attributes
    val a2 = e2.attributes

    if(strict)
      a1.equals(a2)
    else {
      def contains(a: xml.MetaData): Boolean = {
        val v2 = if (a.isPrefixed) a2(a.getNamespace(e1), e2.scope, a.key) else a2(a.key)
        (v2 != null) && (ignoreTextDiffs || v2 == a.value)
      }

      a1.filter(_.value != null).forall(md => contains(md))
    }
  }

  def compare(expected: xml.Node, actual: xml.Node, context: XmlComparisonContext = XmlComparisonContext()): XmlDiffResult = {
    (expected, actual) match {

      case (xml.Comment(_), _) | (_, xml.Comment(_)) =>
        XmlEqual
        
      case (xml.Text(t1), xml.Text(t2)) =>
        if (ignoreTextDiffs || t1.trim == t2.trim)
          XmlEqual
        else
          XmlDifferent(NodeDiff(context.path, expected, actual))
        
      case (e1: xml.Elem, e2: xml.Elem) =>
        val next = context.append(e1)

        if (shouldSkip(next, e1))
          XmlEqual
        else if (isNodeNamesEqual(next, e1, e2)) {
          if (isAttrsEqual(e1, e2))
            compareChildren(next, e1.child.toList, e2.child.toList)
          else
            XmlDifferent(AttributesDiff(next.path, e1.attributes, e2.attributes))
        } else {
          XmlDifferent(NodeDiff(next.path, e1, e2))
        }

      case (e1: xml.Node, e2: xml.Node) =>
        if (ignoreTextDiffs || e1.text == e2.text)
          XmlEqual
        else
          XmlDifferent(NodeDiff(context.path, e1, e2))

    }
  }
}
