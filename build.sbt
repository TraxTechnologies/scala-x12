name := "scala-x12-trax"

organization := "com.trax.platform"

version := "0.1.3"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "io.argonaut" %% "argonaut" % "6.0.4",
  "org.scalaz" %% "scalaz-core" % "7.0.6",
  "org.parboiled" %% "parboiled-scala" % "1.1.6",
  "org.json4s" %% "json4s-jackson" % "3.2.11"
)

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "2.3.12" % "test"
)

resolvers += Resolver.mavenLocal

resolvers += "Trax Artifactory" at "http://artifactory.s03.filex.com/artifactory/repo"

publishTo <<= version { (v: String) =>
  if (v.toString.trim.endsWith("-SNAPSHOT"))
    Some("Artifactory Realm" at "http://artifactory.s03.filex.com/artifactory/libs-snapshot-local")
  else
    Some("Artifactory Realm" at "http://artifactory.s03.filex.com/artifactory/libs-release-local")
}

credentials += Credentials( "Artifactory Realm", "artifactory.s03.filex.com" ,"todd.warwaruk", "AP9yKLrHLgvc7R2tfadFTU5bhj6" )

