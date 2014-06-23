package com.andrewjamesjohnson.x12.config.reader

import com.andrewjamesjohnson.x12.config.{X12Config, X12ConfigNode}
import org.specs2.mutable._

class JsonConfigReaderSpec extends Specification {
  "JsonConfigReader" should {
    "successfully decode valid JSON" in {
      val node = JsonConfigReader.read(getClass.getResource("/example.json"))

      val tree = X12ConfigNode("X12", None, None, None).node(Stream(
        X12ConfigNode("ISA", Some("ISA"), None, None).node(Stream(
          X12ConfigNode("GS", Some("GS"), None, None).node(Stream(
            X12ConfigNode("ST", Some("ST"), Some(List("835")), Some(1)).node(Stream(
              X12ConfigNode("1000A", Some("N1"), Some(List("PR")), Some(1)).leaf,
              X12ConfigNode("1000B", Some("N1"), Some(List("PE")), Some(1)).leaf,
              X12ConfigNode("2000", Some("LX"), None, None).node(Stream(
                X12ConfigNode("2100", Some("CLP"), None, None).node(Stream(
                  X12ConfigNode("2110", Some("SVC"), None, None).leaf
                ))
              ))
            )),
            X12ConfigNode("SE", Some("SE"), None, None).leaf
          )),
          X12ConfigNode("GE", Some("GE"), None, None).leaf
        )),
        X12ConfigNode("IEA", Some("IEA"), None, None).leaf
      ))
      val expected = X12Config(getClass.getResource("/example.json").toString, tree)
      node mustEqual expected
    }
  }
}
