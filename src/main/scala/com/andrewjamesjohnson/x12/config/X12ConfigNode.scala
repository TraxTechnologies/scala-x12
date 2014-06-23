package com.andrewjamesjohnson.x12.config

import com.andrewjamesjohnson.x12.Segment

import scalaz.Tree
import scalaz.syntax.Ops

case class X12ConfigNode(name : String, segmentId : Option[String], segmentQualifiers : Option[List[String]],
                         qualifierPosition : Option[Int]) extends TreeV[X12ConfigNode] {
  def segmentMatches(segment : Segment) : Boolean = {
    segmentId match {
      case None => false
      case Some(s) => segment.name match {
        case `s` =>
          qualifierPosition match {
            case None => true
            case Some(p) =>
              segmentQualifiers.get.contains(segment(p).name)
          }
        case _ => false
      }
    }
  }

  override def self: X12ConfigNode = this
}

trait TreeV[A] extends Ops[A] {
  def node(subForest: Stream[Tree[A]]): Tree[A] = Tree.node(self, subForest)

  def leaf: Tree[A] = Tree.leaf(self)
}