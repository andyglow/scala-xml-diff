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

import com.github.andyglow.xml.diff.XmlDiff

class XmlPatch(details: Seq[XmlDiff.Detail]) {

  def apply(node: xml.NodeSeq): xml.NodeSeq = {
    details.foldLeft(node) {
      case (node: xml.Elem, detail) => detail match {
        case XmlDiff.UnequalName(name, _)        => node.copy(label = name)
        case XmlDiff.UnequalNamespaceUri(uri, _) => node.copy(scope = node.scope.copy(uri = uri))
        case XmlDiff.UnequalElem(label, details) => new XmlPatch(details).apply(node)
        case XmlDiff.UnequalAttribute(k, v, em)  => node % new xml.UnprefixedAttribute(k, v, node.attributes)
        case XmlDiff.RedundantAttribute(k, _)    => node.copy(attributes = node.attributes.remove(k))
        case XmlDiff.AbsentAttribute(k, v)       => node % new xml.UnprefixedAttribute(k, v, node.attributes)
        case XmlDiff.RedundantNode(x)            => node.copy(child = node.child.dropWhile(_ eq x))
        case XmlDiff.AbsentNode(x)               => node.copy(child = node.child :+ x)
      }
      case (node, _)                => node
    }
  }
}

object XmlPatch {

  val empty = new XmlPatch(Seq.empty)

  def apply(details: Seq[XmlDiff.Detail]): XmlPatch = new XmlPatch(details)

  def apply(head: XmlDiff.Detail, tail: Seq[XmlDiff.Detail]): XmlPatch = new XmlPatch(head +: tail)
}