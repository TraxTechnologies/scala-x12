package com.andrewjamesjohnson.x12

case class Segment(value : String, elementSeparator : Char, compositeElementSeparator : Char) extends X12[Element, Element] {
  val elementSeparatorStr = elementSeparator.toString
  lazy val pieces = value.split(elementSeparator).map(e => Element(e, compositeElementSeparator))

  override def children: Seq[Element] = pieces

  override def length: Int = pieces.length

  override def iterator: Iterator[Element] = pieces.iterator

  override def name: String = pieces(0).name

  override def apply(idx: Int): Element = pieces(idx)

  override def toString(): String = pieces.map(_.toString()).mkString(elementSeparatorStr)
}
