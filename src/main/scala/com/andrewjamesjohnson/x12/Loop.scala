package com.andrewjamesjohnson.x12

case class Loop(name : String, segments : Seq[Segment], loops : Seq[Loop]) extends X12[Loop, Segment] {

  override def children: Seq[Loop] = loops

  override def length: Int = segments.length

  override def iterator: Iterator[Segment] = segments.iterator
}
