name := "GettingURL"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies +=  "com.typesafe.akka" %% "akka-http" % "10.0.7"
libraryDependencies += "com.typesafe.play" % "play-ws_2.11" % "2.5.15"
libraryDependencies += "org.jsoup" %  "jsoup" % "1.6.1"
libraryDependencies += "com.google.code.gson" % "gson" % "2.3.1"
libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4"
//libraryDependencies += "org.json" % "json" % "20090211"
libraryDependencies += "com.googlecode.json-simple" % "json-simple" % "1.1.1"
libraryDependencies += "net.liftweb" % "lift-webkit_2.10" % "3.0-M0" % "provided"
libraryDependencies += "org.json4s" % "json4s-native_2.11" % "3.5.2"
libraryDependencies += "org.json4s" % "json4s-jackson_2.11" % "3.5.2"


libraryDependencies += "org.mongodb" %% "casbah" % "3.1.1"
