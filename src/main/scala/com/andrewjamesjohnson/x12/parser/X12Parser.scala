package com.andrewjamesjohnson.x12.parser

import java.net.URL
import java.util.regex.Pattern

import com.andrewjamesjohnson.x12.config.{TreeV, X12Config, X12ConfigNode}
import com.andrewjamesjohnson.x12.{Document, Loop, Segment}

import scala.collection._
import scala.collection.mutable.ListBuffer
import scala.io.Source
import scalaz.{Tree, TreeLoc}

object X12Parser {
  val MIN_SIZE = 106
  val SEGMENT_SEP_POS = 105
  val ELEMENT_SEP_POS = 3
  val COMPOSITE_ELEMENT_SEP_POS = 104

  def parse(fileName : String, config : X12Config) : Document = parse(Source.fromFile(fileName).mkString, fileName, config)

  def parse(url : URL, config : X12Config) : Document = parse(Source.fromURL(url).mkString, url.toString, config)

  def parse(input : String, name : String, config : X12Config) : Document = {
    if (input.size < MIN_SIZE) throw new RuntimeException(name + " is too short to be an X12 document!")

    val segmentSeparator = input(SEGMENT_SEP_POS)
    val elementSeparator = input(ELEMENT_SEP_POS)
    val compositeElementSeparator = input(COMPOSITE_ELEMENT_SEP_POS)

    val quotedSeparator = Pattern.quote(segmentSeparator.toString)
    val segmentDelimiter = "%1$s\r\n|%1$s\n|%1$s".format(quotedSeparator)

    val segments = input.split(segmentDelimiter).map(s => Segment(s, elementSeparator, compositeElementSeparator)).toList
    val matchedSegments = mutable.LinkedHashMap((for(segment <- segments)
      yield (segment, getMatchingConfigNode(config.tree.loc, segment))):_*)
    buildDocument(matchedSegments, segments, config, name, segmentSeparator)
  }

  private def buildDocument(matchedSegments: mutable.LinkedHashMap[Segment, Option[TreeLoc[X12ConfigNode]]],
                   segments : List[Segment], config : X12Config, name : String, segmentSeparator : Char) : Document = {
    val rootForest = ListBuffer[Tree[ParseTreeNode]]()
    var remaining = segments
    config.tree.subForest.foreach(tree => {
      val (t, r) = buildTree(matchedSegments, remaining, tree.loc)
      remaining = r
      rootForest += t
    })

    Document(name, rootForest.result().map(buildLoop(_, segmentSeparator)))
  }

  private def buildLoop(tree : Tree[ParseTreeNode], segmentSeparator : Char) : Loop = {
    val name = tree.rootLabel.config.name
    val segments = tree.rootLabel.segments
    tree.loc.hasChildren match {
      case false => Loop(name, segments, Nil, segmentSeparator)
      case true => Loop(name, segments, tree.subForest.map(buildLoop(_, segmentSeparator)), segmentSeparator)
    }
  }

  private def buildTree(matchedSegments: mutable.LinkedHashMap[Segment, Option[TreeLoc[X12ConfigNode]]],
                         segments : List[Segment], node : TreeLoc[X12ConfigNode]) : (Tree[ParseTreeNode], List[Segment]) = {
    val (currSegments, rest) = segments.span(s => belongsToCurrent(node, matchedSegments(s)))
    node.hasChildren match {
      case false => (ParseTreeNode(currSegments, node.getLabel).leaf, rest)
      case true =>
        var next = rest
        var nextNode = matchedSegments(next.head)
        val childLoops = ListBuffer[Tree[ParseTreeNode]]()
        while (isChild(node, nextNode)) {
          val (tree, s) = buildTree(matchedSegments, next, nextNode.get)
          childLoops += tree
          next = s
          nextNode = matchedSegments(next.head)
        }

        (ParseTreeNode(currSegments, node.getLabel).node(childLoops.result().toStream), next)
    }
  }

  private def isChild(node : TreeLoc[X12ConfigNode], matched : Option[TreeLoc[X12ConfigNode]]) : Boolean = matched match {
    case None => false
    case Some(loc) => loc.parents.map(_._2).contains(node.getLabel)
  }

  private def belongsToCurrent(node : TreeLoc[X12ConfigNode], matched : Option[TreeLoc[X12ConfigNode]]) : Boolean = matched match {
    case None => true
    case Some(loc) => loc.getLabel == node.getLabel
  }

  private def getMatchingConfigNode(root : TreeLoc[X12ConfigNode], segment : Segment) : Option[TreeLoc[X12ConfigNode]] = {
    root.getLabel.segmentMatches(segment) match {
      case true => Some(root)
      case _ => root.find(loc => loc.getLabel.segmentMatches(segment))
    }
  }
}

case class ParseTreeNode(segments : List[Segment], config : X12ConfigNode) extends TreeV[ParseTreeNode] {
  override def self: ParseTreeNode = this
}
