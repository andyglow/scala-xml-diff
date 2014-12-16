package scalax.xml.diff

import scala.xml.{UnprefixedAttribute => Attr}
import org.scalatest._

class XmlComparatorSpec extends FlatSpec with Matchers {

  "<foo/>" must "be <foo/>" in {
    val x1 = <foo/>
    val x2 = <foo/>
    XmlComparator(x1, x2) should be(NoDiff)
  }

  "<foo key=\"val\"/>" must "be <foo key=\"val\"/>" in {
    val x1 = <foo key="val"/>
    val x2 = <foo key="val"/>
    XmlComparator(x1, x2) should be(NoDiff)
  }

  "<foo><bar key=\"val\"/></foo>" must "be <foo><bar key=\"val\"/></foo>" in {
    val x1 = <foo><bar key="val"/></foo>
    val x2 = <foo><bar key="val"/></foo>
    XmlComparator(x1, x2) should be(NoDiff)
  }

  "<foo key=\"val1\"/>" must " not be <foo key=\"val2\"/>" in {
    val x1 = <foo key="val1"/>
    val x2 = <foo key="val2"/>
    XmlComparator(x1, x2) match {
      case diff@AttributesDiff(_,
        xml.UnprefixedAttribute("key", Seq(xml.Text("val1")), xml.Null),
        xml.UnprefixedAttribute("key", Seq(xml.Text("val2")), xml.Null)
      ) =>
        println(diff)
        assert(true)
      case _ => assert(false)
    }
  }

  "<foo key1=\"val1\" key2=\"val2\"/>" must " not be <foo key1=\"val1\"/>" in {
    val x1 = <foo key1="val1" key2="val2"/>
    val x2 = <foo key1="val1"/>
    XmlComparator(x1, x2) match {
      case diff@AttributesDiff(_,
        Attr("key1", Seq(xml.Text("val1")), Attr("key2", Seq(xml.Text("val2")), xml.Null)),
        Attr("key1", Seq(xml.Text("val1")), xml.Null)
      ) =>
        println(diff)
        assert(true)
      case _ => assert(false)
    }
  }

  "<foo><bar/></foo>" must " not be <foo><baz/></foo>" in {
    val x1 = <foo><bar/></foo>
    val x2 = <foo><baz/></foo>
    XmlComparator(x1, x2) match {
      case diff@NodeNotFound(_, <bar/>) =>
        println(diff)
        assert(true)
      case diff@_ =>
        println(diff)
        assert(false)
    }
  }

  "<foo><bar key=\"val1\"/></foo>" must " not be <foo><bar key=\"val2\"/></foo>" in {
    val x1 = <foo><bar key="val1"/></foo>
    val x2 = <foo><bar key="val2"/></foo>
    XmlComparator(x1, x2) match {
      case diff@ChildrenDiff(_, _, List(AttributesDiff(_,
        Attr("key", Seq(xml.Text("val1")), xml.Null),
        Attr("key", Seq(xml.Text("val2")), xml.Null)
      ))) =>
        println(diff)
        assert(true)
      case diff@_ =>
        println(diff)
        assert(false)
    }
  }

  "<foo><bar key1=\"val1\" key2=\"val2\"/></foo>" must " not be <foo><bar key1=\"val1\"/></foo>" in {
    val x1 = <foo><bar key1="val1" key2="val2"/></foo>
    val x2 = <foo><bar key1="val1"/></foo>
    XmlComparator(x1, x2) match {
      case diff@ChildrenDiff(_, _, List(AttributesDiff(_,
      Attr("key1", Seq(xml.Text("val1")), Attr("key2", Seq(xml.Text("val2")), xml.Null)),
      Attr("key1", Seq(xml.Text("val1")), xml.Null)
      ))) =>
        println(diff)
        assert(true)
      case diff@_ =>
        println(diff)
        assert(false)
    }
  }

  "<foo><bar key1=\"val1\"/></foo>" must " not be (strictly) <foo><bar key1=\"val1\" key2=\"val2\"/></foo>" in {
    val x1 = <foo><bar key1="val1"/></foo>
    val x2 = <foo><bar key1="val1" key2="val2"/></foo>
    XmlComparator(strict = true)(x1, x2) match {
      case diff@ChildrenDiff(_, _, List(AttributesDiff(_,
        Attr("key1", Seq(xml.Text("val1")), xml.Null),
        Attr("key1", Seq(xml.Text("val1")), Attr("key2", Seq(xml.Text("val2")), xml.Null))
      ))) =>
        println(diff)
        assert(true)
      case diff@_ =>
        println(diff)
        assert(false)
    }
  }


}
