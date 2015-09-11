package com.andrewjamesjohnson.x12

import com.andrewjamesjohnson.x12.parser.grammar.SegmentNode
import org.json4s.JsonAST.{JObject, JValue}
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._

case class Segment(segmentNode: SegmentNode, elementSeparator : String, compositeElementSeparator : String) extends X12[Element, Element] {
  lazy val pieces = segmentNode.elements.map(Element(_, compositeElementSeparator))

  override def children: Seq[Element] = pieces

  override def length: Int = pieces.length

  override def iterator: Iterator[Element] = pieces.iterator

  override def name: String = pieces.head.name

  override def apply(idx: Int): Element = pieces(idx)

  override def toString(): String = pieces.map(_.toString()).mkString(elementSeparator)

  def debug(indent: Int): Unit = {
    for (i <- 1 to indent) print("\t")
    println("Segment start: " + name)
    pieces.foreach(_.debug(indent + 1))
    for (i <- 1 to indent) print("\t")
    println("Segment end: " + name)
  }

  def toOldJson: JValue = {
    name -> render(children.drop(1).map(_.toOldJson))
  }

  def toJson: JObject = {
    name -> render(children.map(_.toJson))
  }
}
