package com.andrewjamesjohnson.x12.config.reader

import java.net.URL

import argonaut.Argonaut._
import argonaut._
import com.andrewjamesjohnson.x12.config.X12ConfigNode

import scala.io.Source
import scalaz.-\/

object JsonConfigReader extends X12ConfigReader {
  implicit def X12ConfigNodeDecodeJson : DecodeJson[X12ConfigNode] =
    DecodeJson(c => for {
      name <- (c --\ "name").as[String]
      segmentId <- (c --\ "segment_id").as[Option[String]]
      segmentQualifiers <- (c --\ "segment_qualifiers").as[Option[List[String]]]
      qualifierPosition <- (c --\ "qualifier_position").as[Option[Int]]
      children <- (c --\ "children").as[Option[List[X12ConfigNode]]]
    } yield X12ConfigNode(name, segmentId, segmentQualifiers, qualifierPosition, children))

  override def read(fileName: String): X12ConfigNode = decodeJson(Source.fromFile(fileName).mkString)

  override def read(url: URL): X12ConfigNode = decodeJson(Source.fromURL(url).mkString)

  private def decodeJson(input : String) : X12ConfigNode = {
    input.decodeOption[X12ConfigNode] match {
      case Some(x) => x
      case _ => throw new RuntimeException("Invalid JSON provided")
    }
  }

  override def validate(fileName: String): Option[String] = validateJson(Source.fromFile(fileName).mkString)

  override def validate(url: URL): Option[String] = validateJson(Source.fromURL(url).mkString)

  private def validateJson(input : String): Option[String] = {
    input.parse match {
      case -\/(s) => Some(s)
      case _ => None
    }
  }
}
