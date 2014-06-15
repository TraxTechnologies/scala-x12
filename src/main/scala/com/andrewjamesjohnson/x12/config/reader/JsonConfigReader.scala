package com.andrewjamesjohnson.x12.config.reader

import java.net.URL

import com.andrewjamesjohnson.x12.config.X12ConfigNode

import scala.io.Source

import argonaut._
import Argonaut._

object JsonConfigReader extends X12ConfigReader {
  implicit def X12ConfigNodeDecodeJson : DecodeJson[X12ConfigNode] =
    DecodeJson(c => for {
      name <- (c --\ "name").as[String]
      segmentId <- (c --\ "segment_id").as[Option[String]]
      segmentQualifiers <- (c --\ "segment_qualifiers").as[Option[List[String]]]
      qualifierPosition <- (c --\ "qualifier_position").as[Option[Int]]
      children <- (c --\ "children").as[Option[List[X12ConfigNode]]]
    } yield X12ConfigNode(name, segmentId, segmentQualifiers, qualifierPosition, children))

  override def read(fileName: String): X12ConfigNode = {
    decodeJson(Source.fromFile(fileName).mkString)
  }

  override def read(url: URL): X12ConfigNode = {
    decodeJson(Source.fromURL(url).mkString)
  }

  def decodeJson(input : String) = {
    input.decodeOption[X12ConfigNode] match {
      case Some(x) => x
      case _ => throw new RuntimeException("Invalid JSON provided")
    }
  }
}
