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
    "org.scalaxb" %% "scalaxb" % "1.1.2",
    "net.databinder.dispatch" %% "dispatch-core" % "0.9.5"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    scalacOptions ++= Seq("-feature")
  )
/*
  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA,
    settings = Defaults.defaultSettings ++ scalaxbSettings).settings(
      packageName in scalaxb in Compile := "models",
      sourceManaged in scalaxb in Compile := file("app"),
      sourceGenerators in Compile <+= scalaxb in Compile
  )*/ 
  
}
