package com.andrewjamesjohnson.x12.parser.grammar

import org.parboiled.errors.{ErrorUtils, ParsingException}
import org.parboiled.scala._

sealed trait AstNode
case class ValueNode(value: String) extends AstNode
case class ElementNode(pieces: List[ValueNode]) extends AstNode
case class SegmentNode(elements: List[ElementNode]) extends AstNode
case class DocumentNode(segments: List[SegmentNode]) extends AstNode

case class X12ParserGrammar(segmentSeparator: String, elementSeparator: String, compositeElementSeparator: String) extends Parser {

  def Separator(sep: String): Rule0 = rule { (sep ~ "\n") | (sep ~ "\r\n") | sep }

  def WhiteSpace: Rule0 = rule { zeroOrMore(anyOf(" \n\r\t\f")) }

  def ValueChar: Rule0 = rule { noneOf(segmentSeparator + elementSeparator + compositeElementSeparator) }

  def CompositeElementSeparator: Rule1[List[ValueNode]] = rule { compositeElementSeparator ~> { s: String => List(ValueNode(s)) } }

  def Value: Rule1[ValueNode] = rule { (oneOrMore(ValueChar) | EMPTY ~ !EOI ) ~> ValueNode }

  def Element: Rule1[ElementNode] = rule { (CompositeElementSeparator | oneOrMore(Value, compositeElementSeparator)) ~~> ElementNode }

  def Segment: Rule1[SegmentNode] = rule { oneOrMore(Element, elementSeparator) ~~> SegmentNode }

  def Document: Rule1[DocumentNode] = rule { oneOrMore(Segment, Separator(segmentSeparator)) ~~> DocumentNode }

  def parseX12(contents: String): DocumentNode = {
    val parseResult = RecoveringParseRunner(Document).run(contents)

    parseResult.result match {
      case Some(d) => d
      case None => throw new ParsingException(s"Invalid X12 source:\n${ErrorUtils.printParseErrors(parseResult)}")
    }
  }
}
