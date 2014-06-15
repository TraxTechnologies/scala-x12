package com.andrewjamesjohnson.x12.config.reader

import java.net.URL

import com.andrewjamesjohnson.x12.config.X12ConfigNode

trait X12ConfigReader {
  def read(fileName : String) : X12ConfigNode
  def read(url : URL) : X12ConfigNode
}
