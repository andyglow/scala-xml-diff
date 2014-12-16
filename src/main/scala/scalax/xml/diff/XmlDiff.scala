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
package scalax.xml.diff

sealed trait XmlDiff

case object NoDiff extends XmlDiff

sealed trait TheDiff extends XmlDiff {

  def path: List[xml.Node]

  // some sugar
  implicit class RichNode(n: xml.Node) {
    def name: String = n.nameToString(new StringBuilder).toString()
  }

}

case class IllegalNodeFound(path: List[xml.Node], node: xml.Node) extends TheDiff {
  override def toString = s"""IllegalNodeFound(
      |   ${node}
      |)""".stripMargin
}

case class NodeNotFound(path: List[xml.Node], node: xml.Node) extends TheDiff {
  override def toString = s"""NodeNotFound(
      |   ${node}
      |)""".stripMargin
}

case class NodeDiff(path: List[xml.Node], expected: xml.Node, actual: xml.Node) extends TheDiff {
  override def toString = s"""NodeDiff(
      |   Expected: ${expected}
      |   Found: ${actual}
      |)""".stripMargin
}

case class AttributesDiff(path: List[xml.Node], expected: xml.MetaData, actual: xml.MetaData) extends TheDiff {
  override def toString = s"""AttributesDiff(
      |   Expected: ${expected.asAttrMap}
      |   Found: ${actual.asAttrMap}
      |)""".stripMargin
}

case class ChildrenDiff(path: List[xml.Node], element: xml.Node, list: List[XmlDiff]) extends TheDiff {
  override def toString = s"""ChildrenDiff(
      |   None of the elements found fully matched ${element}
      |${list.mkString(",\n").lines.map("   " + _).mkString("\n")}
      |)""".stripMargin
}