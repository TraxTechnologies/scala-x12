package com.andrewjamesjohnson.x12.config.reader

import java.net.URL

import argonaut.Argonaut._
import argonaut._
import com.andrewjamesjohnson.x12.config.{X12Config, X12ConfigNode}

import scala.io.Source
import scalaz.{-\/, Tree, \/-}

object JsonConfigReader extends X12ConfigReader {
  implicit def X12ConfigNodeDecodeJson : DecodeJson[X12ConfigNode] =
    DecodeJson(c => for {
      name <- (c --\ "name").as[String]
      segmentId <- (c --\ "segment_id").as[Option[String]]
      segmentQualifiers <- (c --\ "segment_qualifiers").as[Option[List[String]]]
      qualifierPosition <- (c --\ "qualifier_position").as[Option[Int]]
    } yield X12ConfigNode(name, segmentId, segmentQualifiers, qualifierPosition))


  override def read(fileName: String): X12Config = decodeJson(fileName, Source.fromFile(fileName).mkString)

  override def read(url: URL): X12Config = decodeJson(url.toString, Source.fromURL(url).mkString)

  private def decodeJson(name : String, input : String) : X12Config = {
    input.parse match {
      case -\/(error) => throw new RuntimeException(error)
      case \/-(json) => X12Config(name, createTree(json))
    }
  }

  private def createTree(json : Json) : Tree[X12ConfigNode] = {
    val currentNode = json.as[X12ConfigNode].toDisjunction match {
      case -\/(error) => throw new RuntimeException("%s => %s".format(error._1, error._2.toString()))
      case \/-(n) => n
    }

    json.hasField("children") match {
      case false => currentNode.leaf
      case true => currentNode.node(json.fieldOrEmptyArray("children").array.get.map(c => createTree(c)).toStream)
    }
  }
}
