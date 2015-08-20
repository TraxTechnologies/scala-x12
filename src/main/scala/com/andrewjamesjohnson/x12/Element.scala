package com.andrewjamesjohnson.x12

import com.andrewjamesjohnson.x12.parser.grammar.ElementNode

case class Element(elementNode: ElementNode, compositeElementSeparator: String) extends X12[String, String] {
  lazy val pieces = elementNode.pieces.map(_.value)

  override def children: Seq[String] = pieces

  override def length: Int = pieces.length

  override def iterator: Iterator[String] = pieces.iterator

  override def name: String = pieces(0)

  override def apply(idx: Int): String = pieces(idx)

  override def toString(): String = pieces.mkString(compositeElementSeparator)

  def debug(indent: Int): Unit = {
    for (i <- 1 to indent) print("\t")
    println(s"Element $name: " + pieces)
  }
}
