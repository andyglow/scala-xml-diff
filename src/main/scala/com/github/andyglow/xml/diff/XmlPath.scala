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

case class XmlPath(elems: List[String]) {

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

  def apply(path: String): XmlPath = XmlPath(parse(path))

  val WILDCARD = "*"

  private def parse(path: String): List[String] = path.split('/')
    .map(_.trim)
    .filter(!_.isEmpty)
    // remove duplicated *
    .foldRight(Nil: List[String]) {(token, list) => list match {
      case h :: t if h == WILDCARD && token == WILDCARD => list
      case _ => token :: list
    }}

}
