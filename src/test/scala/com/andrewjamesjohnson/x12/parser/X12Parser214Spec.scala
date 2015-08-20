package com.andrewjamesjohnson.x12.parser

import com.andrewjamesjohnson.x12.config.reader.JsonConfigReader
import org.specs2.mutable.Specification

import scala.io.Source

class X12Parser214Spec extends Specification {
  "X12Parser214" should {
    "decode an X12 file according to a given configuration" in {
      val config = JsonConfigReader.read(getClass.getResource("/example214.json"))
      val document = X12Parser.parse(getClass.getResource("/example214.x12"), config)
      val input = Source.fromURL(getClass.getResource("/example214.x12")).mkString
      document.debug()
      input mustEqual document.toString()
    }
  }
}
