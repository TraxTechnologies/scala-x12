name := "scala-x12"

version := "0.1"

libraryDependencies ++= Seq(
  "io.argonaut" %% "argonaut" % "6.0.4",
  "org.scalaz" %% "scalaz-core" % "7.0.6"
)

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "2.3.12" % "test"
)
    