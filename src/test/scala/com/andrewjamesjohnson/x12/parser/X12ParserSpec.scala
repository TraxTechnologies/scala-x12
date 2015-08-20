package com.andrewjamesjohnson.x12.parser

import com.andrewjamesjohnson.x12.config.reader.JsonConfigReader
import org.specs2.mutable.Specification

import scala.io.Source

class X12ParserSpec extends Specification {
  "X12Parser" should {
    "decode an X12 file according to a given configuration" in {
      val config = JsonConfigReader.read(getClass.getResource("/example.json"))
      val document = X12Parser.parse(getClass.getResource("/example835.txt"), config)
      val input = Source.fromURL(getClass.getResource("/example835.txt")).mkString
      document.debug()
      input mustEqual document.toString()
    }
  }
}
