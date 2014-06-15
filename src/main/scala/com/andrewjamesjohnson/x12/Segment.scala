package com.andrewjamesjohnson.x12

import java.util.regex.Pattern

case class Segment(value : String, elementSeparator : String, compositeElementSeparator : String) extends X12[Element, Element] {
  lazy val pieces = value.split(Pattern.quote(elementSeparator)).map(e => Element(e, compositeElementSeparator))

  override def children: Seq[Element] = pieces

  override def length: Int = pieces.length

  override def iterator: Iterator[Element] = pieces.iterator

  override def name: String = pieces(0).name
}
