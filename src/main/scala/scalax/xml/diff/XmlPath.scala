/* Copyright (c) 2011 Jay A. Patel <jay@patel.org.in>
 * Copyright (c) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package scalax.xml.diff


/**
 * A really simple XPath expression. It supports '/' and '//' and only element nodes.
 * Examples: /feed/author, //updated, /feed//entry
 */
class XmlPath(xpath: String) {

  // remove leading '/'
  private val normalizedPath = if (xpath.startsWith("/")) xpath.substring(1) else xpath
  
  /** 
   * A list of elements that should be matched by this path. '*' matches any element, 
   * any number of times.
   */
  var elems: List[String] = normalizedPath.split('/').toList map { e => 
    if (e.length == 0) XmlPath.WILDCARD
    else e
  }
  // remove double, consecutive '*' in the path
  elems = elems.foldRight(Nil: List[String]) {(e, res) => 
    if (!res.isEmpty && res.head == "*" && e == "*") res else e :: res  
  }

  /** Returns true if this XPath matches the given path. */
  def matches(other: List[String]): Boolean = {
    var xs = elems
    var ys = other
    
    while (ys != Nil && xs != Nil) {
      if (xs.head == XmlPath.WILDCARD) {
        // skip any number of non-matching elements from 'other'
        while (ys != Nil && xs.tail.head != ys.head) ys = ys.tail
        xs = xs.tail
      } else if (xs.head == ys.head) {
          xs = xs.tail
          ys = ys.tail
      } else
        return false
    }
    xs == Nil
  }
  
  override def toString = elems.mkString("", "/", "")
}

object XmlPath {
  final val WILDCARD = "*"
}
