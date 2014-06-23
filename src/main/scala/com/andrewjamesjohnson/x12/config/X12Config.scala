package com.andrewjamesjohnson.x12.config

import scalaz.Tree

case class X12Config(name : String, tree : Tree[X12ConfigNode])
