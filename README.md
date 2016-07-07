# Scala XML Diff
[![Build Status](https://travis-ci.org/andyglow/scala-xml-diff.svg)](https://travis-ci.org/andyglow/scala-xml-diff)
[![Download](https://api.bintray.com/packages/andyglow/scala-tools/scala-xml-diff/images/download.svg) ](https://bintray.com/andyglow/scala-tools/scala-xml-diff/_latestVersion)
[![Coverage Status](https://coveralls.io/repos/github/andyglow/scala-xml-diff/badge.svg?branch=master)](https://coveralls.io/github/andyglow/scala-xml-diff?branch=master)

Tool to compare `scala.xml.Node`s with detailed comparison result

## Usage

### build.sbt
```
libraryDependencies += "com.github.andyglow" %% "scala-xml-diff" % ${LATEST_VERSION} % Compile
```

#### Import
```scala
import com.github.andyglow.xml.diff._
```

#### REPL example
```scala
scala> <foo/> =?= <foo/>
res0: com.github.andyglow.xml.diff.package.XmlDiff = Eq

scala> <foo/> =?= <bar/>
res1: com.github.andyglow.xml.diff.package.XmlDiff = Neq(List(UnequalName(foo,bar)))

scala> <foo x="a"/> =?= <foo x="b"/>
res2: com.github.andyglow.xml.diff.package.XmlDiff = Neq(List(UnequalAttribute(x,a,b)))

scala> <foo><bar key="val1" key2="val2"/></foo> =?= <foo><bar key="val2" key3="val3"/></foo>
res3: com.github.andyglow.xml.diff.package.XmlDiff = Neq(List(
  UnequalElem(bar,List(
    UnequalAttribute(key,val1,val2),
    AbsentAttribute(key2,val2),
    RedundantAttribute(key3,val3)))))
```

#### Scalatest example
```scala
import org.scalatest.xml.XmlMatchers._
```

```scala
"MyRestService" must {
  "generate proper xml" in {
    val document: xml.NodeSeq = service.call(...)
    document should beXml(
    <document>
      <title>Invoice</title>
      <version>0.6.2</version>
      <header></header>
      <content>
        <line id="...">...</line>
      </content>
    </document>
    )
  }
  
  // it is also possible to match negative scenarios
  "be able to distinguish <bar/> from <foo/>" in {
    <bar/> should not beXml <foo/>
  }
}
```