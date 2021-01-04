# Scala XML Diff
[![Build Status](https://cloud.drone.io/api/badges/andyglow/scala-xml-diff/status.svg)](https://cloud.drone.io/andyglow/scala-xml-diff)
[![codecov](https://codecov.io/gh/andyglow/scala-xml-diff/branch/master/graph/badge.svg?token=iEGY6XNcz0)](https://codecov.io/gh/andyglow/scala-xml-diff)
[![mvn](https://img.shields.io/badge/dynamic/json.svg?label=mvn&query=%24.response.docs%5B0%5D.latestVersion&url=https%3A%2F%2Fsearch.maven.org%2Fsolrsearch%2Fselect%3Fq%3Dscala-xml-diff_2.13%26start%3D0%26rows%3D1)](https://search.maven.org/artifact/com.github.andyglow/scala-xml-diff_2.13/)

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
res1: com.github.andyglow.xml.diff.package.XmlDiff = Neq(List(UnequalElem(foo,List(UnequalName(foo,bar)))))

scala> <foo x="a"/> =?= <foo x="b"/>
res2: com.github.andyglow.xml.diff.package.XmlDiff = Neq(List(UnequalElem(foo,List(UnequalAttribute(x,a,b)))))

scala> <foo><bar key="val1" key2="val2"/></foo> =?= <foo><bar key="val2" key3="val3"/></foo>
res3: com.github.andyglow.xml.diff.package.XmlDiff = Neq(List(
  UnequalElem(foo,List(
    UnequalElem(bar,List(
      UnequalAttribute(key,val1,val2),
      AbsentAttribute(key2,val2),
      RedundantAttribute(key3,val3)))))))
      
// not ignoring whitespace      
scala> <jaz>foo</jaz> =?= <jaz>foo </jaz>
res4: com.github.andyglow.xml.diff.package.XmlDiff = Neq(List(
  UnequalElem(jaz,List(
    AbsentNode(foo),
    RedundantNode(foo )))))

// ignoring whitespace      
scala> <jaz>foo</jaz> =#= <jaz>foo </jaz>
res5: com.github.andyglow.xml.diff.package.XmlDiff = Eq
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
  
  // you then can match ignoring whitespace
  "match not counting leading/trailing whitespaces" in {
    <foo>x</foo> should beXml (<foo> x </foo>, ignoreWhitespaces = true)
  }
  
  // the same could be done using not beXml
  
}
```