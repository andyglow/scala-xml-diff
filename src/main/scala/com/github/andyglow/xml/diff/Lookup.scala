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


sealed trait Lookup {

  def apply(right: xml.Node): Boolean
}

object Lookup {

  def apply(node: xml.Node): Lookup = node match {
    case node: xml.Elem => Elem(node)
    case _              => Node(node)
  }

  case class Elem(left: xml.Elem) extends Lookup {
    def apply(right: xml.Node): Boolean = {
      left.namespace == right.namespace &&
        left.label == right.label
    }
  }

  case class Node(left: xml.Node) extends Lookup {
    def apply(right: xml.Node): Boolean = {
      left.text == right.text
    }
  }
}