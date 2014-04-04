import sbt._
import Keys._
import play.Project._
import sbtscalaxb.Plugin._
import ScalaxbKeys._

object ApplicationBuild extends Build {

  val appName         = "impex-portal"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    cache,
    "org.scalaxb" %% "scalaxb" % "1.1.2",
    "net.databinder.dispatch" %% "dispatch-core" % "0.9.5",
    "org.scala-lang.modules" %% "scala-async" % "0.9.0-M4",
    "com.wordnik" %% "swagger-play2" % "1.3.4",
    "com.wordnik" %% "swagger-play2-utils" % "1.3.4",
    "org.scalatest" %%"scalatest" % "1.9.1" % "test",
    "com.typesafe.akka" %% "akka-testkit" % "2.2.0" % "test",
    "org.mockito" % "mockito-all" % "1.9.5"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    scalacOptions ++= Seq("-feature"),
    resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  )
/*
  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA,
    settings = Defaults.defaultSettings ++ scalaxbSettings).settings(
      packageName in scalaxb in Compile := "models",
      sourceManaged in scalaxb in Compile := file("app"),
      sourceGenerators in Compile <+= scalaxb in Compile
  )*/ 
  
}
