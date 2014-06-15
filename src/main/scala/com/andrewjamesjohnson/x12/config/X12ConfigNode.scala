package com.andrewjamesjohnson.x12.config

case class X12ConfigNode(name : String, segmentId : Option[String], segmentQualifiers : Option[List[String]],
                         qualifierPosition : Option[Int], children : Option[List[X12ConfigNode]])
