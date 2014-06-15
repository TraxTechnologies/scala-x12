package com.andrewjamesjohnson.x12

case class Element(value : String, compositeElementSeparator : Char) extends X12[String, String] {
  val compositeElementStr = compositeElementSeparator.toString
  lazy val pieces = value match {
    case `compositeElementStr` => Array(value)
    case _ => value.split(compositeElementSeparator)
  }

  override def children: Seq[String] = pieces

  override def length: Int = pieces.length

  override def iterator: Iterator[String] = pieces.iterator

  override def name: String = pieces(0)

  override def apply(idx: Int): String = pieces(idx)

  override def toString(): String = pieces.mkString(compositeElementStr)
}
