package com.andrewjamesjohnson.x12

case class Loop(name : String, segments : Seq[Segment], loops : Seq[Loop], segmentSeparator : Char) extends X12[Loop, Segment] {
  val segmentSeparatorStr = segmentSeparator.toString
  
  override def children: Seq[Loop] = loops

  override def length: Int = segments.length

  override def iterator: Iterator[Segment] = segments.iterator

  override def apply(idx: Int): Segment = segments(idx)

  override def toString(): String = {
    val segmentString = segments.map(_.toString()).mkString(segmentSeparatorStr + "\n")
    loops.size match {
      case 0 => segmentString + segmentSeparatorStr
      case _ => segmentString + segmentSeparatorStr + "\n" + loops.map(_.toString()).mkString("\n")
    }
  }
}
