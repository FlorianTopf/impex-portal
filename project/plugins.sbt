// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += Resolver.sonatypeRepo("public")

// Use the Play sbt plugin for Play projects
addSbtPlugin("play" % "sbt-plugin" % "2.1.3")
//"play" %% "play" % "2.1.3" exclude("org.scala-lang", "scala-reflect") 

addSbtPlugin("org.scalaxb" % "sbt-scalaxb" % "1.1.2")

