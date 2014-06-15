package com.andrewjamesjohnson.x12

import java.util.regex.Pattern

case class Element(value : String, compositeElementSeparator : String) extends X12[String, String] {
  lazy val pieces = value.split(Pattern.quote(compositeElementSeparator))

  override def children: Seq[String] = pieces

  override def length: Int = pieces.length

  override def iterator: Iterator[String] = pieces.iterator

  override def name: String = pieces(0)
}
