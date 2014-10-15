import sbt._
import Keys._
//import sbtscalaxb.Plugin._
import play.Play.autoImport._
import play.twirl.sbt.Import._
import PlayKeys._
//import ScalaxbKeys._

object ApplicationBuild extends Build {

  val appName         = "impex-portal"
  val appVersion      = "1.0-SNAPSHOT"
    
  scalaVersion:= "2.10.4"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    cache,
    ws,
    "se.radley" %% "play-plugins-salat" % "1.3.0",
    "org.scalaxb" %% "scalaxb" % "1.1.2",
    "net.databinder.dispatch" %% "dispatch-core" % "0.9.5",
    "org.scala-lang.modules" %% "scala-async" % "0.9.2",
    "com.wordnik" %% "swagger-play2" % "1.3.10",
    "com.wordnik" %% "swagger-play2-utils" % "1.3.10",
    "org.scalatest" %% "scalatest" % "1.9.1" % "test",
    "com.typesafe.akka" %% "akka-testkit" % "2.2.0" % "test",
    "org.mockito" % "mockito-all" % "1.9.5",
    "com.typesafe" %% "play-plugins-mailer" % "2.2.0",
    "net.tanesha.recaptcha4j" % "recaptcha4j" % "0.0.7",
    "commons-io" % "commons-io" % "2.3"
  )

  val main = Project(appName, file(".")).enablePlugins(play.PlayScala).settings(
    // Add your own project settings here
    version := appVersion,
    libraryDependencies ++= appDependencies,
    routesImport += "se.radley.plugin.salat.Binders._",
    scalacOptions ++= Seq("-feature"),
    resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    TwirlKeys.templateImports += "org.bson.types.ObjectId"
  )
/*
  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA,
    settings = Defaults.defaultSettings ++ scalaxbSettings).settings(
      packageName in scalaxb in Compile := "models",
      sourceManaged in scalaxb in Compile := file("app"),
      sourceGenerators in Compile <+= scalaxb in Compile
  )*/ 
  
}
