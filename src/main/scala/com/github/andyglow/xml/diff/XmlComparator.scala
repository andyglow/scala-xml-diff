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
    actual: List[xml.Node]): XmlDiff = {

    expected match {
      case e :: tail =>
        e match {
          case e: xml.Elem if !shouldSkip(context, e) => findMatchingNode(context, tail, actual, e)
          case xml.Text(_t) if _t.trim.isEmpty => NoDiff // ???
          case _ => compare(e, actual.head, context)
        }
      case Nil => NoDiff
    }
  }

  private def findMatchingNode(
    context: XmlComparisonContext,
    expected: List[xml.Node],
    actual: List[xml.Node],
    e: xml.Node): XmlDiff = {

    val comparison = actual.filter {
      case a: xml.Node => isNodeNamesEqual(context, e, a)
      case _ => false
    } map { n =>
      val diff = compare(e, n, context)
      (n, diff)
    }

    if (comparison.isEmpty)
      NodeNotFound(context.path, e)
    else {
      val sortOfSimilar = comparison.map(_._2)
      val matched = sortOfSimilar.find(_ == NoDiff)
      matched match {
        case Some(NoDiff) => compareChildren(context, expected, actual.dropWhile(n=>comparison.exists(_._1 == n)))
        case _ => ChildrenDiff(context.path, e, sortOfSimilar)
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

  def compare(expected: xml.Node, actual: xml.Node, context: XmlComparisonContext = XmlComparisonContext()): XmlDiff = {
    (expected, actual) match {

      case (xml.Comment(_), _) | (_, xml.Comment(_)) =>
        NoDiff
        
      case (xml.Text(t1), xml.Text(t2)) =>
        if (ignoreTextDiffs || t1.trim == t2.trim)
          NoDiff 
        else
          NodeDiff(context.path, expected, actual)
        
      case (e1: xml.Elem, e2: xml.Elem) =>
        val next = context.append(e1)

        if (shouldSkip(next, e1))
          NoDiff
        else if (isNodeNamesEqual(next, e1, e2)) {
          if (isAttrsEqual(e1, e2))
            compareChildren(next, e1.child.toList, e2.child.toList)
          else
            AttributesDiff(next.path, e1.attributes, e2.attributes)
        } else {
          NodeDiff(next.path, e1, e2)
        }

      case (e1: xml.Node, e2: xml.Node) =>
        if (ignoreTextDiffs || e1.text == e2.text)
          NoDiff 
        else
          NodeDiff(context.path, e1, e2)

    }
  }
}
