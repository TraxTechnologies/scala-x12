package com.andrewjamesjohnson.x12.config

import scalaz.{Equal, Tree}
import scalaz.Tree._

case class X12Config(name : String, tree : Tree[X12ConfigNode]) {
  override def equals(other : Any): Boolean = other match {
    case that : X12Config =>
      val nameEquals = this.name == that.name
      implicit def x12ConfigNodeEqual = Equal.equalA[X12ConfigNode]
      implicit def x12TreeEquals = treeEqual[X12ConfigNode]
      nameEquals && implicitly[Equal[Tree[X12ConfigNode]]].equal(this.tree, that.tree)

    case _ => false
  }

  override def canEqual(other: Any): Boolean = other.isInstanceOf[X12Config]
}