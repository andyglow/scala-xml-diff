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

sealed trait XmlPath {
  def head: XmlPath.NameMatcher
  def tail: XmlPath
  def isEmpty: Boolean
  def matches(path: List[String]): Boolean
  def ::(head: XmlPath.NameMatcher) = XmlPath.::(head, this)
}

object XmlPath {

  sealed trait NameMatcher extends (String => Boolean)
  object NameMatcher {
    case object Wildcard extends NameMatcher {
      def apply(x: String): Boolean = true
      override def toString: String = "*"
    }
    case class Text(name: String) extends NameMatcher {
      def apply(x: String): Boolean = x == name
      override def toString: String = name
    }
    def apply(text: String): NameMatcher = text.trim match {
      case "*" => Wildcard
      case x => Text(x)
    }
  }

  case object Nil extends XmlPath {
    override def isEmpty: Boolean = true
    override def head: NameMatcher = throw new NoSuchElementException("head of empty xml path")
    override def tail: XmlPath = throw new UnsupportedOperationException("tail of empty xml path")
    override def equals(that: Any) = that.isInstanceOf[Nil.type]
    override def matches(path: List[String]): Boolean = path.isEmpty
    override def toString: String = ""
  }

  case class ::(head: NameMatcher, tail: XmlPath) extends XmlPath {
    override def toString: String = tail.toString match {
      case "" => head.toString()
      case x => head + "/" + x
    }
    override def isEmpty: Boolean = false
    override def matches(path: List[String]): Boolean = {
      def tokenMatches(thatHead: String): (Boolean, List[String]) = head match {
        case NameMatcher.Wildcard if !tail.isEmpty  => (true, path.dropWhile(!tail.head(_)))
        case NameMatcher.Wildcard                   => (true, scala.Nil)
        case NameMatcher.Text(thisHead)             => (thatHead equals thisHead, path.tail)
      }

      val matches = for {
        thatHead            <- path.headOption
        (headMatches, rest) = tokenMatches(thatHead) if headMatches
        result              = tail.matches(rest)
      } yield result

      matches getOrElse false
    }
  }

  def apply(path: String): XmlPath = parse(path)

  private def parse(path: String): XmlPath =
    path
      .split('/')
      .map(_.trim)
      .filter(!_.isEmpty)
      .foldRight(Nil: XmlPath) { (token, list) => list match { // remove duplicated *
        case h :: t if h == NameMatcher.Wildcard && token == "*" => list
        case _ => NameMatcher(token) :: list
      }}

}