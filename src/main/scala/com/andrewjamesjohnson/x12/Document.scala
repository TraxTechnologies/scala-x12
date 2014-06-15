package com.andrewjamesjohnson.x12

case class Document(name : String, loops : Seq[Loop]) extends X12[Loop, Loop] {

  override def children: Seq[Loop] = loops

  override def length: Int = loops.length

  override def iterator: Iterator[Loop] = loops.iterator

  override def apply(idx: Int): Loop = loops(idx)

  override def toString(): String = {
    loops.map(_.toString()).mkString("\n")
  }
}
