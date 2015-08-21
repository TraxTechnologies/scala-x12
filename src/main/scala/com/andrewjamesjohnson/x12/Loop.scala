package com.andrewjamesjohnson.x12

import org.json4s.JsonAST.{JArray, JValue}

case class Loop(name : String, segments : Seq[Segment], loops : Seq[Loop], segmentSeparator : String) extends X12[Loop, Segment] {
  override def children: Seq[Loop] = loops

  override def length: Int = segments.length

  override def iterator: Iterator[Segment] = segments.iterator

  override def apply(idx: Int): Segment = segments(idx)

  override def toString(): String = {
    val segmentString = segments.map(_.toString()).mkString(segmentSeparator + "\n")
    loops.size match {
      case 0 => segmentString + segmentSeparator
      case _ => segmentString + segmentSeparator + "\n" + loops.map(_.toString()).mkString("\n")
    }
  }

  def debug(indent: Int): Unit = {
    for (i <- 1 to indent) print("\t")
    println("Loop start: " + name)
    segments.foreach(_.debug(indent + 1))
    loops.foreach(_.debug(indent + 1))
    for (i <- 1 to indent) print("\t")
    println("Loop end: " + name)
  }

  def toJson: JValue = {
    JArray((segments.map(_.toJson) ++ loops.map(_.toJson)).toList)
  }
}
