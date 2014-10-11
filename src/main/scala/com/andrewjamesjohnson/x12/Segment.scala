package com.andrewjamesjohnson.x12

import com.andrewjamesjohnson.x12.parser.grammar.SegmentNode

case class Segment(segmentNode: SegmentNode, elementSeparator : String, compositeElementSeparator : String) extends X12[Element, Element] {
  lazy val pieces = segmentNode.elements.map(Element(_, compositeElementSeparator))

  override def children: Seq[Element] = pieces

  override def length: Int = pieces.length

  override def iterator: Iterator[Element] = pieces.iterator

  override def name: String = pieces(0).name

  override def apply(idx: Int): Element = pieces(idx)

  override def toString(): String = pieces.map(_.toString()).mkString(elementSeparator)
}
