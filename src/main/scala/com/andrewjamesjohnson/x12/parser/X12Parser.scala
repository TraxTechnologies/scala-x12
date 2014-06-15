package com.andrewjamesjohnson.x12.parser

import java.net.URL
import java.util.regex.Pattern

import com.andrewjamesjohnson.x12.config.X12ConfigNode
import com.andrewjamesjohnson.x12.{Loop, Document, Segment}

import scala.collection.mutable
import scala.io.Source

object X12Parser {
  val MIN_SIZE = 106
  val SEGMENT_SEP_POS = 105
  val ELEMENT_SEP_POS = 3
  val COMPOSITE_ELEMENT_SEP_POS = 104

  def parse(fileName : String, config : X12ConfigNode) : Document = parse(Source.fromFile(fileName).mkString, fileName, config)

  def parse(url : URL, config : X12ConfigNode) : Document = parse(Source.fromURL(url).mkString, url.toString, config)

  def parse(input : String, name : String, config : X12ConfigNode) : Document = {
    if (input.size < MIN_SIZE) throw new RuntimeException(name + " is too short to be an X12 document!")

    val segmentSeparator = input(SEGMENT_SEP_POS)
    val elementSeparator = input(ELEMENT_SEP_POS)
    val compositeElementSeparator = input(COMPOSITE_ELEMENT_SEP_POS)

    val quotedSeparator = Pattern.quote(segmentSeparator.toString)
    val segmentDelimiter = "%1$s\r\n|%1$s\n|%1$s".format(quotedSeparator)

    val segments = input.split(segmentDelimiter).map(s => Segment(s, elementSeparator, compositeElementSeparator))
    val matchedSegments = segments.map(s => (s, getMatchingConfigNode(config, s))).toMap
    val nodeSegmentMap = mutable.Map[X12ConfigNode, List[Segment]]()

    var currSegments = new mutable.ListBuffer[Segment]()
    var lastNode = matchedSegments(segments.head).get
    segments.foreach(segment =>
      matchedSegments(segment) match {
        case None => currSegments += segment
        case Some(n) =>
          nodeSegmentMap(lastNode) = currSegments.result()
          currSegments.clear()
          currSegments += segment
          lastNode = n
      })
    nodeSegmentMap(lastNode) = currSegments.result()

    buildDocument(segmentSeparator, name, config, nodeSegmentMap)
  }

  private def buildDocument(segmentSeparator : Char, name : String, root : X12ConfigNode, nodeSegmentMap : mutable.Map[X12ConfigNode, List[Segment]]) : Document = {
    Document(name, root.children.get.map(c => buildLoop(segmentSeparator, c, nodeSegmentMap)).flatten)
  }

  private def buildLoop(segmentSeparator : Char, root : X12ConfigNode, nodeSegmentMap : mutable.Map[X12ConfigNode, List[Segment]]) : Option[Loop] = {
    nodeSegmentMap.get(root) match {
      case None => None
      case Some(segments) => root.children match {
        case None => Some(Loop(root.name, segments, List[Loop](), segmentSeparator))
        case Some(cs) =>
          Some(Loop(root.name, segments, cs.map(c => buildLoop(segmentSeparator, c, nodeSegmentMap)).flatten, segmentSeparator))
      }
    }
  }

  private def getMatchingConfigNode(root : X12ConfigNode, segment : Segment) : Option[X12ConfigNode] = {
    loopMatches(root, segment) match {
      case true => Some(root)
      case _ => root.children match {
        case Some(cs) => cs.map(c => getMatchingConfigNode(c, segment)).flatten match {
          case Nil => None
          case xs => Some(xs.head)
        }
        case None => None
      }
    }
  }

  private def loopMatches(node : X12ConfigNode, segment : Segment) : Boolean = {
    node.segmentId match {
      case None => false
      case Some(s) => segment.name match {
        case `s` =>
          node.qualifierPosition match {
            case None => true
            case Some(p) =>
              node.segmentQualifiers.get.contains(segment(p).name)
          }
        case _ => false
      }
    }
  }
}
