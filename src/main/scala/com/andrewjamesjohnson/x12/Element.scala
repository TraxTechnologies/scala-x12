package com.andrewjamesjohnson.x12

import com.andrewjamesjohnson.x12.parser.grammar.ElementNode
import org.json4s.JsonAST.JValue
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.jackson.JsonMethods._

case class Element(elementNode: ElementNode, compositeElementSeparator: String) extends X12[String, String] {
  lazy val pieces = elementNode.pieces.map(_.value)

  override def children: Seq[String] = pieces

  override def length: Int = pieces.length

  override def iterator: Iterator[String] = pieces.iterator

  override def name: String = pieces.head

  override def apply(idx: Int): String = pieces(idx)

  override def toString(): String = pieces.mkString(compositeElementSeparator)

  def debug(indent: Int): Unit = {
    for (i <- 1 to indent) print("\t")
    println(s"Element $name: " + pieces)
  }

  def toOldOldJson: JValue = {
    render(pieces)
  }

  def toOldJson: JValue = {
    children.size match {
      case 0 => JNothing
      case 1 => render(children.head)
      case _ => render(children)
    }
  }

  def toJson: JValue = {
    children.size match {
      case 0 => JNothing
      case 1 => render(children.head)
      case _ => render(children)
    }
  }
}
