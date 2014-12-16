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