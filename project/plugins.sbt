// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += Resolver.sonatypeRepo("public")

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.6")
//"play" %% "play" % "2.1.3" exclude("org.scala-lang", "scala-reflect") 

addSbtPlugin("org.scalaxb" % "sbt-scalaxb" % "1.1.2")

