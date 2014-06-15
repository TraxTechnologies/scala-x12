package com.andrewjamesjohnson.x12

trait X12[T, S] extends Seq[S] {
  def children : Seq[T]

  def name : String
}