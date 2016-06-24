# Scala XML Diff
[![Build Status](https://travis-ci.org/andyglow/scala-xml-diff.svg)](https://travis-ci.org/andyglow/scala-xml-diff)
[![Download](https://api.bintray.com/packages/andyglow/scala-tools/scala-xml-diff/images/download.svg) ](https://bintray.com/andyglow/scala-tools/scala-xml-diff/_latestVersion)

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
scala> <foo/> compareTo <foo/>
res0: com.github.andyglow.xml.diff.XmlDiff = NoDiff

scala> <foo/> compareTo <bar/>
res1: com.github.andyglow.xml.diff.XmlDiff = NodeDiff(
   Expected: <foo/>
   Found: <bar/>
)

scala> <foo x="a"/> compareTo <foo x="b"/>
res2: com.github.andyglow.xml.diff.XmlDiff = AttributesDiff(
   Expected: Map(x -> a)
   Found: Map(x -> b)
)
```

_TODO_
- provide scalatest matchers